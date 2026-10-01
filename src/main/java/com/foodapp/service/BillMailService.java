package com.foodapp.service;

import com.foodapp.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Emails a copy of every generated bill to the restaurant's order-desk
 * mailbox (see restaurant.order-mail-to in application.properties) the
 * moment a customer places an order.
 *
 * Mail sending is best-effort: if SMTP isn't configured yet, or the
 * mail server is briefly unreachable, we log the failure and let the
 * order go through as normal rather than blocking checkout.
 */
@Service
public class BillMailService {

    private static final Logger log = LoggerFactory.getLogger(BillMailService.class);

    private final JavaMailSender mailSender;

    @Value("${restaurant.order-mail-to}")
    private String orderMailTo;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public BillMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBillEmail(Order order, String billText) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromAddress != null && !fromAddress.isBlank()) {
                message.setFrom(fromAddress);
            }
            message.setTo(orderMailTo);
            String tableSuffix = (order.getTableNumber() != null && !order.getTableNumber().isBlank())
                    ? " [Table " + order.getTableNumber() + "]" : "";
            message.setSubject("New order #" + order.getId() + " - " + order.getUser().getName()
                    + " (Rs. " + String.format("%.2f", order.getTotalAmount()) + ")" + tableSuffix);
            message.setText(billText);
            mailSender.send(message);
            log.info("Bill for order #{} emailed to {}", order.getId(), orderMailTo);
        } catch (Exception e) {
            // Never fail order placement just because the mail server rejected/timed out.
            log.error("Could not email bill for order #{} to {}: {}", order.getId(), orderMailTo, e.getMessage());
        }
    }
}
