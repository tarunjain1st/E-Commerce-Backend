package com.example.paymentservice.clients;

import com.example.paymentservice.dtos.CheckoutItem;
import com.example.paymentservice.dtos.OrderInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderClient {
    public OrderInfo getOrder(Long orderId) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(orderId);
        orderInfo.setCustomerEmail("test@test.test");
        CheckoutItem checkoutItem1 = new CheckoutItem();
        checkoutItem1.setProductName("Nokia 3310");
        checkoutItem1.setQuantity(2l);
        checkoutItem1.setUnitAmount(1000l);
        CheckoutItem checkoutItem2 = new CheckoutItem();
        checkoutItem2.setProductName("Iphone 13");
        checkoutItem2.setQuantity(3l);
        checkoutItem2.setUnitAmount(100l);
        List<CheckoutItem> checkoutItems = new ArrayList<>();
        checkoutItems.add(checkoutItem1);
        checkoutItems.add(checkoutItem2);
        orderInfo.setItems(checkoutItems);
        return orderInfo;
    }
}
