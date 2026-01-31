package com.example.paymentservice.paymentgateways;

import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentGateway implements IPaymentGateway {

    @Autowired
    private RazorpayClient razorpayClient;

    @Override
    public String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description) {
        try{
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount",amount);
            paymentLinkRequest.put("currency","INR");
            paymentLinkRequest.put("accept_partial",true);
            paymentLinkRequest.put("first_min_partial_amount",100);
            paymentLinkRequest.put("reference_id",orderId);
            paymentLinkRequest.put("expire_by",System.currentTimeMillis() / 1000 + 901);
            paymentLinkRequest.put("description",description);
            JSONObject customer = new JSONObject();
            customer.put("name",name);
            customer.put("contact",phoneNumber);
            customer.put("email",email);
            paymentLinkRequest.put("customer",customer);
            JSONObject notify = new JSONObject();
            notify.put("sms",true);
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);
            paymentLinkRequest.put("reminder_enable",true);
            PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
            System.out.println(payment);
            return payment.get("short_url");
        }catch (RazorpayException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public PaymentResponse createPayment(OrderDto orderDto) {
        return null;
    }

    @Override
    public PaymentSession getPaymentDetails(String sessionId) {
        return null;
    }

}
