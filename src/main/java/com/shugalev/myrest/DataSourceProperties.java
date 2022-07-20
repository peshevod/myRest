package com.shugalev.myrest;

/**
 *
 * @author ilya
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/*
    Cofiguration parameters for DataSource
*/

@ConfigurationProperties(prefix = "database")
@Configuration("dataSourceProperties")
@DependsOn("myLogger")
public class DataSourceProperties {
    private String connectURI;
    private String driver;
    private String username;
    private String password;
    
    @Autowired
    private MyLogger myLogger;

    public String getPassword() {
        return password;
    }

    public String getConnectURI() {
        return connectURI;
    }

    public String getUsername() {
        return username;
    }

    public String getDriver() {
        return driver;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectURI(String connectURI) {
        this.connectURI = connectURI;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void logProperties()
    {
//        myLogger.getLogger1().info("user="+username+" connecturi="+connectURI);
    }

}
