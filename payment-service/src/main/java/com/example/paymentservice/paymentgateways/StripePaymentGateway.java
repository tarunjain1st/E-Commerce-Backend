package com.example.paymentservice.paymentgateways;

import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;
import com.example.paymentservice.dtos.PaymentWebhookResponse;
import com.example.paymentservice.exceptions.PaymentGatewayUnavailableException;
import com.example.paymentservice.models.PaymentStatus;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.model.Price;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StripePaymentGateway implements IPaymentGateway {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    // -----------------------------
    // Direct payment link (intentional duplicate)
    // -----------------------------
    private Price createPrice(Long amount, String productName) {
        try {
            PriceCreateParams params = PriceCreateParams.builder()
                    .setCurrency("usd")
                    .setUnitAmount(amount)
                    .setRecurring(
                            PriceCreateParams.Recurring.builder()
                                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                    .build())
                    .setProductData(
                            PriceCreateParams.ProductData.builder()
                                    .setName(productName)
                                    .build())
                    .build();
            return Price.create(params);
        } catch (StripeException ex) {
            throw new PaymentGatewayUnavailableException("Stripe price creation failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description) {
        try {
            Stripe.apiKey = stripeApiKey;
            Price price = createPrice(amount, orderId);

            PaymentLinkCreateParams params = PaymentLinkCreateParams.builder()
                    .addLineItem(
                            PaymentLinkCreateParams.LineItem.builder()
                                    .setPrice(price.getId())
                                    .setQuantity(1L)
                                    .build())
                    .setAfterCompletion(PaymentLinkCreateParams.AfterCompletion.builder()
                            .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                            .setRedirect(PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                    .setUrl("http://localhost.com/")
                                    .build())
                            .build())
                    .build();

            return com.stripe.model.PaymentLink.create(params).getUrl();
        } catch (StripeException e) {
            throw new PaymentGatewayUnavailableException("Stripe initiatePayment failed: " + e.getMessage(), e);
        }
    }

    // -----------------------------
    // Create payment session for orders
    // -----------------------------
    @Override
    public PaymentResponse createPayment(OrderDto orderDto) {
        try {
            Stripe.apiKey = stripeApiKey;

            List<SessionCreateParams.LineItem> lineItems = orderDto.getItems().stream()
                    .map(item -> SessionCreateParams.LineItem.builder()
                            .setQuantity(item.getQuantity())
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("inr")
                                    .setUnitAmount(item.getUnitAmount() * 100)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(item.getProductName())
                                            .build())
                                    .build())
                            .build())
                    .collect(Collectors.toList());

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setSuccessUrl("http://localhost:8080/api/payments/success?session_id={CHECKOUT_SESSION_ID}&gateway=STRIPE")
                    .setCancelUrl("http://localhost:8080/api/payments/cancel?session_id={CHECKOUT_SESSION_ID}")
                    .addAllLineItem(lineItems)
                    .setCustomerEmail(orderDto.getCustomerEmail())
                    .putMetadata("orderId", orderDto.getOrderId().toString())
                    .build();

            Session session = Session.create(params);

            PaymentResponse response = new PaymentResponse();
            response.setSessionId(session.getId());
            response.setCheckoutUrl(session.getUrl());
            return response;

        } catch (StripeException e) {
            throw new PaymentGatewayUnavailableException("Stripe createPayment failed: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentSession getPaymentDetails(String sessionId) {
        try {
            Stripe.apiKey = stripeApiKey;
            Session session = Session.retrieve(sessionId);
            return mapToPaymentSession(session);
        } catch (Exception e) {
            throw new PaymentGatewayUnavailableException("Failed to fetch Stripe session: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentWebhookResponse parseWebhook(String payload, Map<String, String> headers) {
        try {
            Event event = Webhook.constructEvent(payload, headers.get("Stripe-Signature"), webhookSecret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = deserializer.getObject().orElse(deserializer.deserializeUnsafe());

            if (stripeObject == null) return null;

            PaymentWebhookResponse webhookResponse = new PaymentWebhookResponse();
            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    Session session = (Session) stripeObject;
                    webhookResponse.setPaymentReference(session.getId());
                    webhookResponse.setStatus(PaymentStatus.SUCCESS);
                }
                case "checkout.session.expired" -> {
                    Session session = (Session) stripeObject;
                    webhookResponse.setPaymentReference(session.getId());
                    webhookResponse.setStatus(PaymentStatus.EXPIRED);
                }
                default -> {
                    return null;
                }
            }
            return webhookResponse;

        } catch (StripeException e) {
            throw new PaymentGatewayUnavailableException("Stripe webhook verification failed", e);
        }
    }

    private static PaymentSession mapToPaymentSession(Session session) {
        PaymentSession paymentSession = new PaymentSession();
        paymentSession.setSessionId(session.getId());
        paymentSession.setCheckoutUrl(session.getUrl());
        paymentSession.setAmount(session.getAmountTotal() / 100);
        paymentSession.setStatus(session.getPaymentStatus());
        paymentSession.setCustomerEmail(session.getCustomerEmail());
        paymentSession.setExpiresAt(new Date(session.getExpiresAt() * 1000));
        return paymentSession;
    }
}
