package com.alibaba.cloud.ai.headless.auth.api.authentication.pojo;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.headless.auth.api.authentication.constant.UserConstants.*;

@Data
@AllArgsConstructor
public class UserWithPassword extends User {

    private String password;

    public UserWithPassword(Long id, String name, String displayName, String email, String password,
            Integer isAdmin) {
        super(id, name, displayName, email, isAdmin, null);
        this.password = password;
    }

    public static UserWithPassword get(Long id, String name, String displayName, String email,
            String password, Integer isAdmin) {
        return new UserWithPassword(id, name, displayName, email, password, isAdmin);
    }

    public static Map<String, Object> convert(UserWithPassword user) {
        Map<String, Object> claims = new HashMap<>(5);
        claims.put(TOKEN_USER_ID, user.getId());
        claims.put(TOKEN_USER_NAME, StringUtils.isEmpty(user.getName()) ? "" : user.getName());
        claims.put(TOKEN_USER_PASSWORD,
                StringUtils.isEmpty(user.getPassword()) ? "" : user.getPassword());
        claims.put(TOKEN_USER_EMAIL, StringUtils.isEmpty(user.getEmail()) ? "" : user.getEmail());
        claims.put(TOKEN_USER_DISPLAY_NAME, user.getDisplayName());
        claims.put(TOKEN_CREATE_TIME, System.currentTimeMillis());
        claims.put(TOKEN_IS_ADMIN, user.getIsAdmin());
        return claims;
    }
}
