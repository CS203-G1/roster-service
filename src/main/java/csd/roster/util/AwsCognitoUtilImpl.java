package csd.roster.util;

import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import csd.roster.util.interfaces.AwsCognitoUtil;
import org.springframework.beans.factory.annotation.Value;

public class AwsCognitoUtilImpl implements AwsCognitoUtil {
    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.access-key}")
    private String accessKey;
    
    @Value("${aws.access-secret}")
    private String secretKey;

    @Override
    public AdminAddUserToGroupResult addUserToGroup(String userId, String groupName) {
        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest();

        request.setUserPoolId(userPoolId);
        request.setGroupName(groupName);
        request.setUsername(userId);

        return adminAddUserToGroup;
    }
}
