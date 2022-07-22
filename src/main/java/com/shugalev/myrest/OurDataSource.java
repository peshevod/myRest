package com.shugalev.myrest;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "database")
@Configuration
public class OurDataSource {
    @Autowired
    private MyLogger myLogger;
//    @Value("${database.connectURI}")
    private String connectURI;
//    @Value("${database.driver}")
    private String driver;
//    @Value("${database.username}")
    private String username;
//    @Value("${database.password}")
    private String password;

    private Map<String,String> hibernate;

    @Bean
    public DataSource myDataSource()
    {
        return this.configureDataSource();
    };
    public DataSource configureDataSource()
    {
        BasicDataSource ds=new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setUrl(connectURI);
        return ds;
    }

    public void setConnectURI(String connectURI) {
        this.connectURI = connectURI;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Map<String, String> getHibernate() {
        return hibernate;
    }

    public void setHibernate(Map<String, String> hibernate) {
        this.hibernate=new HashMap<>();
        for(String s:hibernate.keySet()) this.hibernate.put("hibernate."+s,hibernate.get(s));
        myLogger.getLogger().info("Hibernate={}",this.hibernate);
    }
}
