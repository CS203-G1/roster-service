package csd.roster.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import org.springframework.beans.factory.annotation.Value;

// Use static methods and fields since they will never change - think of Singleton Objects
public class AwsCognitoUtil {
    @Value("${aws.cognito.userPoolId}")
    private static String userPoolId;

    @Value("${aws.access-key}")
    private static String accessKey;

    @Value("${aws.access-secret}")
    private static String secretKey;

    public static AdminAddUserToGroupResult addUserToGroup(String userId, String groupName) {
        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest();

        request.setUserPoolId(userPoolId);
        request.setGroupName(groupName);
        request.setUsername(userId);

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCognitoIdentityProvider identityProvider = AWSCognitoIdentityProviderClientBuilder.
                standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        return identityProvider.adminAddUserToGroup(request);
    }
}
