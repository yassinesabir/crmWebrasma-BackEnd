package com.crud.RankinDigitalCrud.Service;

import com.crud.RankinDigitalCrud.Entity.Lead;
import com.crud.RankinDigitalCrud.Entity.Notification;
import com.crud.RankinDigitalCrud.Repository.LeadRepository;
import com.crud.RankinDigitalCrud.Repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class); // Define the logger

    @Autowired
    private LeadRepository leadRepository; // Repository to fetch leads

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${company.email}")
    private String companyEmail;

    public void sendNotification(List<Long> leadIds, String subject, String message, String sentBy) {
        List<String> emails = fetchLeadsEmails(leadIds);

        for (String email : emails) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(companyEmail);
                mailMessage.setTo(email);
                mailMessage.setSubject(subject);
                mailMessage.setText(message);

                emailSender.send(mailMessage);
                logger.info("Notification sent to: {}", email);
            } catch (Exception e) {
                logger.error("Error sending email to: {}", email, e);
            }
        }

        // Save notification details to the database
        saveNotificationDetails(leadIds, subject, message, sentBy);
    }

    private List<String> fetchLeadsEmails(List<Long> leadIds) {
        return leadRepository.findAllById(leadIds).stream()
                .map(Lead::getEmail)
                .collect(Collectors.toList());
    }

    private void saveNotificationDetails(List<Long> leadIds, String subject, String message, String sentBy) {
        logger.debug("Saving notification with details: leadIds={}, subject={}, message={}, sentBy={}", leadIds, subject, message, sentBy);

        Notification notification = new Notification();
        notification.setLeadIds(leadIds);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());
        notification.setSentBy(sentBy); // Ensure this is set correctly
        notificationRepository.save(notification);
    }

}
