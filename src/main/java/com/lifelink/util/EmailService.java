package com.lifelink.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/** Sends account-verification messages using SMTP settings supplied through environment variables. */
public final class EmailService {
    private EmailService() { }

    public static boolean isConfigured() {
        return present("SMTP_HOST") && present("SMTP_PORT") && present("SMTP_USER")
                && present("SMTP_PASSWORD") && present("SMTP_FROM");
    }

    public static void sendVerificationEmail(String recipient, String verificationUrl) throws MessagingException {
        if (!isConfigured()) throw new IllegalStateException("SMTP settings are not configured.");
        String host = System.getenv("SMTP_HOST");
        String port = System.getenv("SMTP_PORT");
        String username = System.getenv("SMTP_USER");
        String password = System.getenv("SMTP_PASSWORD");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(System.getenv("SMTP_FROM")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
        message.setSubject("Verify your LifeLink email address", "UTF-8");
        message.setText("Welcome to LifeLink!\n\nPlease verify your email address by opening this link:\n"
                + verificationUrl + "\n\nThis link expires in 24 hours and can be used only once.\n"
                + "If you did not create a LifeLink account, you can ignore this message.", "UTF-8");
        Transport.send(message);
    }

    private static boolean present(String key) {
        String value = System.getenv(key);
        return value != null && !value.trim().isEmpty();
    }
}
