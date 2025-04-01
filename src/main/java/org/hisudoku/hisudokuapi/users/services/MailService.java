package org.hisudoku.hisudokuapi.users.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Locale;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailService {
    private final String NO_REPLY; // "noreply@example.com"
    private final String ACTIVATE_EMAIL_LINK; // "noreply@example.com"
    private final String FORGOT_PASSWORD_LINK; // "noreply@example.com"

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public MailService(
            @Value("${spring.mail.no-reply}") String noReplyAddress,
            @Value("${spring.mail.activate-email-link}") String activateEmailLink,
            @Value("${spring.mail.forgot-password-link}") String forgotPasswordLink,
            JavaMailSender javaMailSender,
            SpringTemplateEngine springTemplateEngine
    ) {
        this.NO_REPLY = noReplyAddress;
        this.mailSender = javaMailSender;
        this.templateEngine = springTemplateEngine;
        this.ACTIVATE_EMAIL_LINK = activateEmailLink;
        this.FORGOT_PASSWORD_LINK = forgotPasswordLink;

        //    Properties props = mailSender.getJavaMailProperties();
        //    prop.put("mail.smtp.auth", true);
        //    prop.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        //    prop.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");
        //    prop.put("mail.smtp.port", "25");
        //    prop.put("mail.smtp.starttls.enable", "true");
        //    props.put("mail.transport.protocol", "smtp");
        //    props.put("mail.debug", "true");
    }

    public void sendResetPasswordByOTT(String email, String userName, String passwordResetLink, String lang) { // throws MessagingException
        Context context = new Context();
        context.setVariable("passwordResetLink", passwordResetLink);
        context.setVariable("userName", userName);

//        Map<String, Object> templateModel = new HashMap<>();
//        templateModel.put("username", "Example username");
//        context.setVariables(templateModel);

        context.setLocale(Locale.forLanguageTag(lang));
        String html = templateEngine.process("mail/password-reset/password-reset.html", context);
        try {
            sendHtml(email, "reset password for account", html);
        } catch (MessagingException e) {
            //  The protocol specs for SMTP in RFC 821 specifies the 550 return code that the SMTP server should return when attempting to send an email to the incorrect address. But most of the public SMTP servers don’t do this. Instead, they send a “delivery failed” email or give no feedback at all. For example, Gmail SMTP server sends a “delivery failed” message. And we get no exceptions in our program. So, we have a few options to handle this case:
            //    Catch the SendFailedException, which can never be thrown.
            //    Check our sender mailbox for the “delivery failed” message for some period of time. This is not straightforward, and the time period is not determined.
            //    If our mail server gives no feedback at all, we can do nothing.
            log.error("Error while sending reset password email => {}", e.getLocalizedMessage());
        }
    }

    public void sendResetPasswordByJWT(String email, String userName, String token, String lang) { // throws MessagingException
        Context context = new Context();
        context.setVariable("passwordResetLink", FORGOT_PASSWORD_LINK + "/token=" + token);
        context.setVariable("userName", userName);
        context.setLocale(Locale.forLanguageTag(lang));
        String html = templateEngine.process("mail/password-reset/password-reset.html", context);
        try {
            sendHtml(email, "reset password for account", html);
        } catch (MessagingException e) {
            log.error("Error while sending reset password email => {}", e.getLocalizedMessage());
        }
    }

    public void sendActivateEmailByUUID(String email, String userName, String token, String lang) {
        Context context = new Context();
        context.setVariable("activationLink", ACTIVATE_EMAIL_LINK + "/token=" + token);
        context.setVariable("userName", userName);
        context.setLocale(Locale.forLanguageTag(lang));
        String activateAccountMailHtml = templateEngine.process("mail/activate-email/activate-email.html", context);
        try {
            sendHtml(email, "activate your account", activateAccountMailHtml);
        } catch (MessagingException e) {
            log.error("Error while sending account activation email => {}", e.getLocalizedMessage());
        }
    }

    public void sendTextMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(NO_REPLY);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void inlineHtml(String to, String subject) throws MessagingException, FileNotFoundException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(NO_REPLY);
        helper.setTo(to);
        helper.setSubject(subject);

        helper.addAttachment("attachment.pdf", ResourceUtils.getFile("classpath:templates/mail/attachments/HiSudoku-attachment.pdf"));
        //    FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        //    helper.addAttachment("Invoice", file);

        //    InputStream attachmentStream = new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));
        //    String attachmentName, InputStream attachmentStream
        //    helper.addAttachment(attachmentName, new InputStreamResource(attachmentStream));

        helper.setText("<html><body><p>Attachment test!</p><img src='cid:logo'></body></html>", true);

        // Ordering is important for inlining we first need to add html text, then resources
        helper.addInline("logo", ResourceUtils.getFile("classpath:static/assets/icon.png"));

        mailSender.send(message);
    }

    public record MailResource(String name, File file) {
    }

    public void sendHtmlMessageWithResources(String to, String subject, String htmlBody, List<MailResource> inlines, List<MailResource> attachments) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(NO_REPLY);
        helper.setTo(to);
        helper.setSubject(subject);

        if (Objects.nonNull(attachments)) {
            for (MailResource attachment : attachments) {
                helper.addAttachment(attachment.name, attachment.file);
            }
        }

        helper.setText(htmlBody, true);

        if (Objects.nonNull(inlines)) {
            for (MailResource inline : inlines) {
                helper.addInline(inline.name, inline.file);
            }
        }
        mailSender.send(message);
    }

    public void sendHtml(String to, String subject, String htmlBody) throws MessagingException {
        sendHtmlMessageWithResources(to, subject, htmlBody, Collections.emptyList(), Collections.emptyList());
    }
}
