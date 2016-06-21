package com.highpowerbear.hpbtrader.shared.common;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@ApplicationScoped
public class EmailSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Resource(name = "java:/mail/ses")
    private Session mailSession;
    
    @Asynchronous
    public void sendEmail(String subject, String content) {
        Message msg = new MimeMessage(mailSession);
        try {
            msg.setFrom(new InternetAddress(HtrDefinitions.EMAIL_FROM));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(HtrDefinitions.EMAIL_TO));
            msg.setSubject(subject);
            msg.setText(content);
            l.info("Sending email, from=" + HtrDefinitions.EMAIL_FROM + ", to=" + HtrDefinitions.EMAIL_TO + ", subject=" + subject);
            Transport.send(msg);
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error sending email", e);
        }
    }
}
