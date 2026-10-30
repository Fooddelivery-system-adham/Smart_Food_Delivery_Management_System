package SWProject;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class EmailService {

    private final Properties mailProps = new Properties();
    private String senderEmail;
    private String senderPassword;

    public EmailService() {
        loadConfig();
    }

    /**
     * Loads SMTP configuration from db.conf file.
     */
    public void loadConfig() {
        Properties config = new Properties();
        boolean loaded = false;

        // 1. تجربة القراءة من المجلد الرئيسي للـ Project
        try (FileInputStream fis = new FileInputStream("db.conf")) {
            config.load(fis);
            loaded = true;
        } catch (IOException ignored) {
            // 2. إذا لم يجده، يجرب القراءة من src/main/resources/db.conf
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.conf")) {
                if (is != null) {
                    config.load(is);
                    loaded = true;
                }
            } catch (IOException e) {
                // Ignore
            }
        }

        if (loaded) {
            this.senderEmail = config.getProperty("email.username", "no-reply@fooddelivery.com");
            this.senderPassword = config.getProperty("email.password", "");
            
            this.mailProps.put("mail.smtp.auth", "true");
            this.mailProps.put("mail.smtp.starttls.enable", "true");
            this.mailProps.put("mail.smtp.host", config.getProperty("email.host", "smtp.gmail.com"));
            this.mailProps.put("mail.smtp.port", config.getProperty("email.port", "587"));
        } else {
            System.out.println("⚠️ Warning: Could not load db.conf for email settings. Using defaults.");
        }
    }

    public boolean sendOrderConfirmation(String recipientEmail, Order order) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return false;
        }

        String subject = "🍔 Order Confirmation & Invoice - Order #" + order.getOrderId();
        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Thank you for your order! Your order has been placed successfully and is now BEING PREPARED.\n\n");
        body.append("=================== INVOICE DETAILS ===================\n");
        body.append("Order ID:          #").append(order.getOrderId()).append("\n");
        body.append("Order Type:        ").append(order.getOrderType()).append("\n");
        body.append("Delivery Address:  ").append(order.getDeliveryAddress()).append("\n");
        body.append("Distance:          ").append(order.getDeliveryDistanceKm()).append(" km\n");
        body.append("-------------------------------------------------------\n");
        body.append("Subtotal:          $").append(String.format("%.2f", order.getSubtotal())).append("\n");
        body.append("Delivery Fee:      $").append(String.format("%.2f", order.getDeliveryFee())).append("\n");
        if (order.getDiscountAmount() > 0) {
            body.append("Discount:         -$").append(String.format("%.2f", order.getDiscountAmount())).append("\n");
        }
        body.append("TOTAL AMOUNT:      $").append(String.format("%.2f", order.getTotal())).append("\n");
        body.append("=======================================================\n\n");
        body.append("We will notify you once your driver is on the way!\n\n");
        body.append("Best regards,\nSmart Food Delivery Management System 🇵🇸");

        return sendMail(recipientEmail, subject, body.toString());
    }


    public boolean sendOrderCompletedEmail(String recipientEmail, int orderId) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return false;
        }

        String subject = "🎉 Order Delivered Successfully - Order #" + orderId;
        String body = "Dear Customer,\n\n"
                + "Your order #" + orderId + " has been successfully DELIVERED to your address!\n"
                + "Thank you for choosing Smart Food Delivery System.  🍕🍔\n\n"
                + "Best regards,\nSmart Food Delivery Management System 🇵🇸";

        return sendMail(recipientEmail, subject, body);
    }

    /**
     * Dispatches the email via SMTP session.
     */
    public boolean sendMail(String recipientEmail, String subject, String body) {
        try {
            Session session = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("📧 [Email Sent] Confirmation email sent to: " + recipientEmail);
            return true;
        } catch (Exception e) {
            System.out.println("📧 [Email Service] Logged notification email for: " + recipientEmail + " (SMTP offline/simulated)");
            return true;
        }
    }
}