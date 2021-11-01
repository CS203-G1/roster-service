package csd.roster.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsMailUtil {
    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.access-secret}")
    private String secretKey;

    public AWSStaticCredentialsProvider awsCredentials() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(credentials);
    }

    @Bean
    public AmazonSimpleEmailService getAmazonSimpleEmailService() {
        return AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(awsCredentials())
                .withRegion("ap-southeast-1").build();
    }

    public void addEmailToPool(String email) {
        AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient();
        
        VerifyEmailIdentityRequest verifyEmailIdentityRequest = new VerifyEmailIdentityRequest();
        verifyEmailIdentityRequest.setEmailAddress(email);
        
        amazonSimpleEmailServiceClient.verifyEmailIdentity(verifyEmailIdentityRequest);
    }

}