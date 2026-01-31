package com.example.paymentservice.paymentgateways;

import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StripePaymentGateway implements IPaymentGateway {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;


    private Price createPrice(Long amount, String productName){
        try {
            PriceCreateParams params =
                    PriceCreateParams.builder()
                            .setCurrency("usd")
                            .setUnitAmount(amount)
                            .setRecurring(
                                    PriceCreateParams.Recurring.builder()
                                            .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                            .build()
                            )
                            .setProductData(
                                    PriceCreateParams.ProductData.builder().setName(productName)
                                            .build()
                            )
                            .build();
            Price price = Price.create(params);
            return price;
        } catch(StripeException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description) {
        try {
            Stripe.apiKey = this.stripeApiKey;

            Price price = createPrice(amount, orderId);

            PaymentLinkCreateParams params =
                    PaymentLinkCreateParams.builder()
                            .addLineItem(
                                    PaymentLinkCreateParams.LineItem.builder()
                                            .setPrice(price.getId())
                                            .setQuantity(1L)
                                            .build()
                            ).setAfterCompletion(PaymentLinkCreateParams.AfterCompletion.builder()
                                    .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                    .setRedirect(PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                            .setUrl("http://localhost.com/").build())
                                    .build())
                            .build();
            PaymentLink paymentLink = PaymentLink.create(params);
            return paymentLink.getUrl();
        }catch (StripeException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public PaymentResponse createPayment(OrderDto orderDto) {
        System.out.println("inside StripePaymentGateway createPayment");
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
                    .setBillingAddressCollection(
                            SessionCreateParams.BillingAddressCollection.REQUIRED
                    )
                    .setSuccessUrl("http://localhost:8080/api/payments/success?session_id={CHECKOUT_SESSION_ID}&gateway=STRIPE")
                    .addAllLineItem(lineItems)
                    .setCustomerEmail(orderDto.getCustomerEmail())
                    .putMetadata("orderId", orderDto.getOrderId().toString())
                    .build();

            Session session = Session.create(params);

            PaymentResponse response = new  PaymentResponse();
            response.setSessionId(session.getId());
            response.setCheckoutUrl(session.getUrl());
            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Stripe error: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentSession getPaymentDetails(String sessionId) {
        try {
            Stripe.apiKey = stripeApiKey;
            Session session = Session.retrieve(sessionId);
            return getPaymentSession(session);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Stripe session: " + e.getMessage(), e);
        }
    }

    private static PaymentSession getPaymentSession(Session session) {
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