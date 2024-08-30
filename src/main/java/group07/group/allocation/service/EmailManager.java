package group07.group.allocation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class EmailManager {

    @Autowired
    private JavaMailSender sender;
    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendEmail(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        sender.send(message);
    }

    /**
     * Sends an HTML email from thymeleaf
     * @param template - path to the template in src/main/resources
     * @param context - context with replacement variables set Context#setVariable("variable", "message")
     */
    @Async
    public void sendTemplateEmail(String recipient, String subject, String template, Context context) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mmh = new MimeMessageHelper(message);
        mmh.setTo(recipient);
        mmh.setText(templateEngine.process(template, context), true);
        mmh.setSubject(subject);
        sender.send(message);
    }
}
