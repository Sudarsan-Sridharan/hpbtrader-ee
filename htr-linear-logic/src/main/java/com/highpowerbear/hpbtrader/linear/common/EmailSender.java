package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.inject.Named;
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
@Named
@Singleton
public class EmailSender {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);

    @Resource(name = "java:/mail/ses")
    private Session mailSession;
    
    @Asynchronous
    public void sendEmail(String subject, String content) {
        Message msg = new MimeMessage(mailSession);
        try {
            msg.setFrom(new InternetAddress(LinSettings.EMAIL_FROM));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(LinSettings.EMAIL_TO));
            msg.setSubject(subject);
            msg.setText(content);
            l.info("Sending email, from=" + LinSettings.EMAIL_FROM + ", to=" + LinSettings.EMAIL_TO + ", subject=" + subject);
            Transport.send(msg);
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error sending email", e);
        }
    }
}
