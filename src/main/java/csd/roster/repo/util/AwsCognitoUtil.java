package csd.roster.repo.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import csd.roster.domain.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AwsCognitoUtil {
    private final String userPoolId;

    private final String clientId;

    private final String accessKey;

    private final String secretKey;

    private final String awsRegion;

    private final String employerUsername;

    private final String employerPassword;

    // AWSCognitoIdentityProvider: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/AWSCognitoIdentityProvider.html
    private final AWSCognitoIdentityProvider identityProvider;

    @Autowired
    public AwsCognitoUtil(@Value("${aws.cognito.userPoolId}") String userPoolId,
                          @Value("${aws.cognito.clientId}") String clientId,
                          @Value("${aws.access-key}") String accessKey,
                          @Value("${aws.access-secret}") String secretKey,
                          @Value("${aws.default-region}") String awsRegion,
                          @Value("${aws.cognito.employerUsername}") String employerUsername,
                          @Value("${aws.cognito.employerPassword}") String employerPassword) {
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.awsRegion = awsRegion;
        this.employerUsername = employerUsername;
        this.employerPassword = employerPassword;

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        this.identityProvider = AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withRegion(this.awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public AdminAddUserToGroupResult addUserToGroup(final String userId,
                                                    final String groupName) {
        AdminAddUserToGroupRequest adminAddUserToGroupRequest = new AdminAddUserToGroupRequest();

        adminAddUserToGroupRequest.setUserPoolId(this.userPoolId);
        adminAddUserToGroupRequest.setGroupName(groupName);
        adminAddUserToGroupRequest.setUsername(userId);

        return identityProvider.adminAddUserToGroup(adminAddUserToGroupRequest);
    }

    public Employee createUser(final Employee employee) {
        // AdminCreateUserRequest: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/model/AdminCreateUserRequest.html
        AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest();

        adminCreateUserRequest.addClientMetadataEntry("email", employee.getEmail());
        adminCreateUserRequest.setUserPoolId(this.userPoolId);
        adminCreateUserRequest.setTemporaryPassword(RandomStringUtils.randomAlphanumeric(10));
        // Cognito requires the username to be email but later on the result username is a uuid
        adminCreateUserRequest.setUsername(employee.getEmail());

        // AdminCreateUserResult: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/model/AdminCreateUserResult.html
        AdminCreateUserResult adminCreateUserResult = identityProvider.adminCreateUser(adminCreateUserRequest);

        // UserType reference: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cognitoidp/model/UserType.html
        // Note that we learnt to not modify parameter variables
        // But we have to get the username (UUID) from Cognito to be persisted in the database
        employee.setId(UUID.fromString(adminCreateUserResult.getUser().getUsername()));

        return employee;
    }

    public String getEmployeeCognitoStatus(final UUID employeeId) {
        AdminGetUserRequest adminGetUserRequest = new AdminGetUserRequest();

        adminGetUserRequest.setUsername(String.valueOf(employeeId));
        adminGetUserRequest.setUserPoolId(this.userPoolId);

        AdminGetUserResult adminGetUserResult = identityProvider.adminGetUser(adminGetUserRequest);

        return adminGetUserResult.getUserStatus();
    }

    // note that this is only used for development purposes
    // strictly for integration testing
    public String authenticateAndGetToken() {
        AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();

        adminInitiateAuthRequest.setAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH);
        adminInitiateAuthRequest.setClientId(this.clientId);
        adminInitiateAuthRequest.setUserPoolId(this.userPoolId);

        Map<String, String> authParameters = new HashMap<String, String>();
        authParameters.put("USERNAME", this.employerUsername);
        authParameters.put("PASSWORD", this.employerPassword);
        adminInitiateAuthRequest.setAuthParameters(authParameters);

        AdminInitiateAuthResult adminInitiateAuthResult = identityProvider.adminInitiateAuth(adminInitiateAuthRequest);

        return adminInitiateAuthResult.getAuthenticationResult().getAccessToken();
    }

    public void deleteUser(final String username) {
        AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest();

        adminDeleteUserRequest.setUsername(username);
        adminDeleteUserRequest.setUserPoolId(this.userPoolId);

        AdminDeleteUserResult adminDeleteUserResult = identityProvider.adminDeleteUser(adminDeleteUserRequest);
    }
}
