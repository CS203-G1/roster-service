package csd.roster.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import csd.roster.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AwsCognitoUtil(@Value("${aws.cognito.userPoolId}") String userPoolId,
                          @Value("${aws.access-key}") String accessKey,
                          @Value("${aws.access-secret}") String secretKey) {
        this.userPoolId = userPoolId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        this.identityProvider = AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public AdminAddUserToGroupResult addUserToGroup(String userId, String groupName) {
        AdminAddUserToGroupRequest adminAddUserToGroupRequest = new AdminAddUserToGroupRequest();

        adminAddUserToGroupRequest.setUserPoolId(this.userPoolId);
        adminAddUserToGroupRequest.setGroupName(groupName);
        adminAddUserToGroupRequest.setUsername(userId);

        return identityProvider.adminAddUserToGroup(adminAddUserToGroupRequest);
    }

    public void createUser(Employee employee) {
        AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest();

        adminCreateUserRequest.addClientMetadataEntry("email", employee.getEmail());
        adminCreateUserRequest.addClientMetadataEntry("password", employee.getPassword());


    }
}
