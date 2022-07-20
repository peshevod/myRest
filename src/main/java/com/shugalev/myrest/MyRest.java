package com.shugalev.myrest;

/**
 *
 * @author ilya
 */

/*
    Main Program
*/
import org.apache.camel.CamelContext;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.TypeConverterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;

import java.util.*;

@SpringBootApplication
//@EnableAutoConfiguration
public class MyRest{

    private CamelContext context;
    private static ApplicationContext applicationContext;
//    @Autowired
//    private IncidentRepository incidentRepository;
    @Autowired
    private MyLogger myLogger;
    public void test() throws Exception
    {
//        Logger log = LoggerFactory.getLogger(MyRest.class);
//        if(myLogger==null) log.info("myLogger={}",myLogger);
         context = new DefaultCamelContext();
        context.getRegistry().bind("myDataSource", applicationContext.getBean("myDataSource"));
        context.getRegistry().bind("entityManagerFactory", applicationContext.getBean("entityManagerFactory"));
        context.getRegistry().bind("transactionManager", applicationContext.getBean("transactionManager"));
//        context.getRegistry().bind("myDataSource", applicationContext.getBean("myDataSource"));
         context.getTypeConverterRegistry().addTypeConverter(Incident.class,Map.class, (TypeConverter) applicationContext.getBean("myConverter"));
        String[] beans=applicationContext.getBeanDefinitionNames();
        for(String s:beans) if(s.toUpperCase().contains("ENTITY"))
        {
            myLogger.getLogger().info(s+" "+
                    applicationContext.getBean(s).getClass().getCanonicalName());
        }
         context.addRoutes(applicationContext.getBean(MyRouteBuilder.class));
         context.start();
        myLogger.getLogger().info("Obect = {}",context.getRegistry().lookupByName("myDataSource"));
        TypeConverter lookup = context.getTypeConverterRegistry().lookup(Incident.class, LinkedHashMap.class);
        if(lookup!=null) myLogger.getLogger().info("lookup={}",lookup);
        else myLogger.getLogger().info("lookup==null");

//        Iterable<Incident> incidents=incidentRepository.findAll();
//        for(Incident inc:incidents) log.info("Record {}",inc.getId());
    }

    public static void main(String[] args) throws Exception {

        applicationContext=SpringApplication.run(MyRest.class, args);
        Thread.sleep(5000);
        ((MyRest)applicationContext.getBean("myRest")).test();

//        MyRest myrest=new MyRest();

    }
    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        registration.setName("CamelServlet");
        return registration;
    }
}
