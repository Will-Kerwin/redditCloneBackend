package co.uk.itskerwin.redditClone.service;

import co.uk.itskerwin.redditClone.exception.SpringRedditException;
import co.uk.itskerwin.redditClone.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
// lombok creates slf4j logger and injects to class
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final MainContentBuilder mainContentBuilder;

    @Async
    public void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            // hardcode the email not advised but due to being fake smtp its ok
            messageHelper.setFrom("springreddit@email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            // calling build method of mailcontenet builder returning in html format
            messageHelper.setText(mainContentBuilder.build(notificationEmail.getBody()));
        };

        try {
            mailSender.send(messagePreparator);
            log.info("Activation Email sent!!");
        } catch (MailException e){
            throw new SpringRedditException("Exception occured when sending mail to " + notificationEmail.getRecipient());
        }
    }
}
