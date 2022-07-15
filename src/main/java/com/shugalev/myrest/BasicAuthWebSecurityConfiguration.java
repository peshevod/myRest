package com.shugalev.myrest;

/**
 *
 * @author ilya
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

@ConfigurationProperties(prefix = "auth")
@Configuration("basicAuthWebSecurityConfiguration")

/*
    Configure Basic Authorization and set Users for it
*/
public class BasicAuthWebSecurityConfiguration
{
    @Autowired
    private UsersCredentials usersCredentials;
    @Autowired
    private AppBasicAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private MyLogger myLogger;
    
    private Map<String,String> basicAuthWebSecurityConfiguration;
    
//    private String path;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        myLogger.getLogger1().info("allowed path="+basicAuthWebSecurityConfiguration.get("path"));
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,basicAuthWebSecurityConfiguration.get("path")).hasRole("USER_ROLE")
                .antMatchers(HttpMethod.PUT,basicAuthWebSecurityConfiguration.get("path")).hasRole("USER_ROLE")
                .antMatchers(HttpMethod.GET,basicAuthWebSecurityConfiguration.get("path")).hasRole("USER_ROLE")
                .antMatchers(HttpMethod.DELETE,basicAuthWebSecurityConfiguration.get("path")).hasRole("USER_ROLE")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                .and().csrf().disable()
                .sessionManagement().disable();
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        List<UserDetails> users = new ArrayList<>();
        for(int i=1; i<=usersCredentials.getUsersNumber(); i++) {
            Map<String, String> userCredentials = usersCredentials.getUserCredentials(i);
            UserDetails user = User.withUsername(userCredentials.get("name")).password(userCredentials.get("password")).roles(userCredentials.get("roles")).build();
            users.add(user);
        }
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
    
    public String getPath()
    {
        return basicAuthWebSecurityConfiguration.get("path");
    }
    
    public Map<String,String> getBasicAuthWebSecurityConfiguration()
    {
        return basicAuthWebSecurityConfiguration;
    }
    
    public void setBasicAuthWebSecurityConfiguration(Map<String,String> basicAuthWebSecurityConfiguration)
    {
        this.basicAuthWebSecurityConfiguration = basicAuthWebSecurityConfiguration;
    }
}
