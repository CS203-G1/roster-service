package csd.roster.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import csd.roster.service.interfaces.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    private AmazonSimpleEmailService amazonSimpleEmailService;
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    public EmailServiceImpl(AmazonSimpleEmailService amazonSimpleEmailService, SpringTemplateEngine springTemplateEngine) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Async
    public void sendEmail(String fromEmail, String companyName, String recipient, String subject) {
        Session session = Session.getInstance(new Properties(System.getProperties()));
        MimeMessage mimeMessage = new MimeMessage(session);
        Context context = new Context();

        // Creation of hashmap to store dynamic variables in email
        Map<String, Object> variables = new HashMap<>();
        context.setVariables(variables);

        try {
            mimeMessage.setSubject(subject);
            mimeMessage.setFrom(fromEmail);
            mimeMessage.setRecipients(RecipientType.TO, InternetAddress.parse(recipient));

            // Multipart is used for emails that contains text body and messages
            MimeMultipart messageBody = new MimeMultipart("alternative");
            MimeBodyPart wrap = new MimeBodyPart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(springTemplateEngine.process("emailTemplate", context), "text/html; charset=UTF-8");
            messageBody.addBodyPart(htmlPart);
            wrap.setContent(messageBody);

            mimeMessage.setContent(messageBody);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
            amazonSimpleEmailService.sendRawEmail(rawEmailRequest);
        } catch (MessageRejectedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEmailToPool(String email) {
        VerifyEmailIdentityRequest verifyEmailIdentityRequest = new VerifyEmailIdentityRequest();
        verifyEmailIdentityRequest.setEmailAddress(email);
        
        amazonSimpleEmailService.verifyEmailIdentity(verifyEmailIdentityRequest);
    }
}
