package csd.roster.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import csd.roster.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AwsCognitoUtil {
    private String userPoolId;

    private String accessKey;

    private String secretKey;

    // AWSCognitoIdentityProvider: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/AWSCognitoIdentityProvider.html
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

    public Employee createUser(Employee employee) {
        // AdminCreateUserRequest: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/model/AdminCreateUserRequest.html
        AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest();

        adminCreateUserRequest.addClientMetadataEntry("email", employee.getEmail());
        adminCreateUserRequest.setUserPoolId(this.userPoolId);
        adminCreateUserRequest.setTemporaryPassword(employee.getPassword());


        // AdminCreateUserResult: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/model/AdminCreateUserResult.html
        AdminCreateUserResult adminCreateUserResult = identityProvider.adminCreateUser(adminCreateUserRequest);

        // UserType reference: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/model/UserType.html
        // Note that we learnt to not modify parameter variables
        // But we have to get the username (UUID) from Cognito to be persisted in the database
        employee.setId(UUID.fromString(adminCreateUserResult.getUser().getUsername()));

        return employee;
    }
}
