package com.shugalev.myrest;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class MyConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource myDataSource()
    {
//        dataSourceProperties.logProperties();
        BasicDataSource ds=new BasicDataSource();
        ds.setDriverClassName(dataSourceProperties.getDriver());
        ds.setUsername(dataSourceProperties.getUsername());
        ds.setPassword(dataSourceProperties.getPassword());
        ds.setUrl(dataSourceProperties.getConnectURI());
        return ds;
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("com.acme.domain");
        factory.setPackagesToScan("com.shugalev.myrest");
        factory.setDataSource(myDataSource());
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    public MyIncidentRepository incidentRepository()
    {
        return new MyIncidentRepository();
    }

}
