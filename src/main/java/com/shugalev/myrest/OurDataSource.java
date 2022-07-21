package com.shugalev.myrest;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class OurDataSource {
    @Autowired
    private MyLogger myLogger;
    @Value("${database.connectURI}")
    private String connectURI;
    @Value("${database.driver}")
    private String driver;
    @Value("${database.username}")
    private String username;
    @Value("${database.password}")
    private String password;

    @Bean
    public DataSource myDataSource()
    {
        return this.configureDataSource();
    };
    public DataSource configureDataSource()
    {
        BasicDataSource ds=new BasicDataSource();
        ds.setDriverClassName(/*dataSourceProperties.getDriver()*/driver);
        ds.setUsername(/*dataSourceProperties.getUsername()*/username);
        ds.setPassword(/*dataSourceProperties.getPassword()*/password);
        ds.setUrl(/*dataSourceProperties.getConnectURI()*/connectURI);
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
}
