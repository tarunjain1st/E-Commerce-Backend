package com.example.notificationservice.templates;

public class EmailLayoutTemplate {

    public static String wrap(String title, String content) {
        return """
        <html>
        <body style="margin:0; padding:0; font-family: Arial, Helvetica, sans-serif; background:#f4f6f8;">
            <div style="
                max-width:600px;
                margin:40px auto;
                background:#ffffff;
                border-radius:8px;
                box-shadow:0 2px 8px rgba(0,0,0,0.08);
                overflow:hidden;
            ">

                <!-- Header -->
                <div style="background:#1e3a8a; padding:18px;">
                    <h2 style="
                        margin:0;
                        text-align:center;
                        color:#ffffff;
                        font-weight:600;
                        letter-spacing:0.5px;
                    ">
                        TarunKart
                    </h2>
                </div>

                <!-- Body -->
                <div style="padding:24px; color:#333333;">
                    <h3 style="margin-top:0; color:#1e3a8a;">%s</h3>
                    <div style="line-height:1.6;">
                        %s
                    </div>
                </div>

                <!-- Footer -->
                <div style="
                    border-top:1px solid #e5e7eb;
                    padding:14px;
                    text-align:center;
                    font-size:12px;
                    color:#6b7280;
                ">
                    © 2026 TarunKart. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """.formatted(title, content);
    }
}
