package csd.roster.services.service.interfaces;

public interface EmailService {
    void sendEmail(String fromEmail, String companyName, String recipient, String subject);
}
