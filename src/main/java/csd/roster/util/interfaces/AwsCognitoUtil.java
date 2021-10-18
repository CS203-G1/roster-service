package csd.roster.util.interfaces;

import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;

public interface AwsCognitoUtil {
    AdminAddUserToGroupResult addUserToGroup(String userId, String groupName);
}
