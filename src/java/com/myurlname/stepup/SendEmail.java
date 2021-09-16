package com.myurlname.stepup;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * SendEmail is a static class that knows how to send emails!
 * @author gabriel
 */
public class SendEmail {

    private static Properties mailServerProperties;
    private static Session getMailSession;
    private static MimeMessage generateMailMessage;

    private static void setupEmail () {
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
    }

    public static void generateAndSendEmail(String toAddress,String subject,
                                            String emailBody) {

        try {
            setupEmail();

            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            generateMailMessage.setSubject(subject);
            generateMailMessage.setContent(emailBody, "text/html");

            Transport transport = getMailSession.getTransport("smtp");

            // Enter your correct gmail UserID and Password
            // if you have 2FA enabled then provide App Specific Password
            transport.connect("smtp.gmail.com", "stepup.acc.email@gmail.com", "javauser99");
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
        }
        catch (MessagingException me) {}
    }
}
