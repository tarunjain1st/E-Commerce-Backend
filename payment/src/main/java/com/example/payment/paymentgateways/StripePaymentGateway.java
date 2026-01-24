package com.example.payment.paymentgateways;

import com.example.payment.dtos.SessionDto;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class StripePaymentGateway implements IPaymentGateway {

    @Value("${stripe.apiKey}")
    private String StripeApiKey;


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
            Stripe.apiKey = this.StripeApiKey;

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

    @Override
    public SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities) {
        //Add your implementation here
        try {
            Stripe.apiKey = StripeApiKey;
            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            for (int i = 0; i < productNames.size(); i++){
                String productName = productNames.get(i);
                Long amount = amounts.get(i);
                Long quantity = quantities.get(i);
                SessionCreateParams.LineItem lineItem =
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(quantity)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(productName)
                                                                .build()
                                                )
                                                .setUnitAmount(amount)
                                                .build()
                                )
                                .build();
                lineItems.add(lineItem);
            }
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setSuccessUrl(successUrl)
                            .addAllLineItem(lineItems)
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .build();
            Session session = Session.create(params);
            return from(session);
        } catch (StripeException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    private SessionDto from (Session session) {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(session.getId());
        sessionDto.setUrl(session.getUrl());
        sessionDto.setExpiry(session.getExpiresAt());
        sessionDto.setTotal(session.getAmountTotal());
        return sessionDto;
    }
}