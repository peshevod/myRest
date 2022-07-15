package com.shugalev.myrest;

/**
 *
 * @author ilya
 */
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

/*
    Cofiguration parameters for Users Credentials
*/

@ConfigurationProperties(prefix = "security")
@Configuration("usersCredentials")
public class UsersCredentials {
    private Map<String,Map<String,String>> usersCredentials;

    public Map<String,Map<String, String>> getUsersCredentials() {
        return usersCredentials;
    }

    public void setUsersCredentials(Map<String,Map<String, String>> usersCredentials) {
        this.usersCredentials = usersCredentials;
    }

     public Map<String,String> getUserCredentials(int n) {
         return usersCredentials.get("user" + n);
     }

    public int getUsersNumber()
    {
        if(usersCredentials!=null) return usersCredentials.size(); else return 0;
    }
 }
