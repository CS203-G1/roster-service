package csd.roster.service.interfaces;

public interface EmailService {
    void sendEmail(String fromEmail, String companyName, String recipient, String subject);
    void addEmailToPool(String email);
}
