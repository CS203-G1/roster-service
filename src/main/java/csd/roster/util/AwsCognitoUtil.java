package csd.roster.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsCognitoUtil {
    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.access-secret}")
    private String secretKey;

    private AWSCognitoIdentityProvider identityProvider;

    public AwsCognitoUtil() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        this.identityProvider = AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public AdminAddUserToGroupResult addUserToGroup(String userId, String groupName) {
        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest();

        request.setUserPoolId(this.userPoolId);
        request.setGroupName(groupName);
        request.setUsername(userId);

        return identityProvider.adminAddUserToGroup(request);
    }

    public void createUser() {

    }
}
