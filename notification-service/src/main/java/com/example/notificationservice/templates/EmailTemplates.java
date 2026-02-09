package com.example.notificationservice.templates;

public class EmailTemplates {

    public static String userCreated(String name) {
        String content = """
            <p>Hello <b>%s</b>,</p>
            <p>Welcome to <b>TarunKart</b>! Your account has been created successfully.</p>
            <p>You can now log in and start exploring our products:</p>
            <p><a href="http://localhost:8080/api/auth/login">Log In to Your Account</a></p>
            <p>We’re excited to have you with us.</p>
        """.formatted(name);

        return EmailLayoutTemplate.wrap("Welcome to TarunKart", content);
    }

    public static String passwordReset(String name, String resetLink) {
        String content = """
        <p>Hello <b>%s</b>,</p>
        <p>You recently requested to reset your password.</p>
        <p>Click the link below to set a new password:</p>
        <p><a href="%s">Reset Password</a></p>
        <p>This link is valid for the next <b>10 minutes</b>.</p>
        <p>If you did not request a password reset, please ignore this email or contact our support team.</p>
    """.formatted(name, resetLink);

        return EmailLayoutTemplate.wrap("Password Reset – TarunKart", content);
    }



    public static String orderPlaced(String name, Long orderId, Double amount) {
        String content = """
            <p>Hello <b>%s</b>,</p>
            <p>Thank you for shopping with <b>TarunKart</b>!</p>
            <p>Your order with ID <b>%s</b> has been placed successfully.</p>
            <p><b>Order Total:</b> ₹%s</p>
            <p>We’ll notify you once your order is shipped.</p>
        """.formatted(name, orderId, amount);

        return EmailLayoutTemplate.wrap("Order Confirmed – TarunKart", content);
    }

    public static String paymentSuccess(String name, String paymentId, Double amount) {
        String content = """
            <p>Hello <b>%s</b>,</p>
            <p>Your payment has been processed successfully.</p>
            <p><b>Payment ID:</b> %s</p>
            <p><b>Amount Paid:</b> ₹%s</p>
            <p>Thank you for choosing <b>TarunKart</b>.</p>
        """.formatted(name, paymentId, amount);

        return EmailLayoutTemplate.wrap("Payment Successful – TarunKart", content);
    }
}
