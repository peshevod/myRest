package com.shugalev.myrest;

/**
 *
 * @author ilya
 */

/*
    Main Program
*/
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;

import java.util.*;

@SpringBootApplication
public class MyRest{

    @Autowired
    private CamelContext camelContext;
    private static ApplicationContext applicationContext;
    @Autowired
    private MyLogger myLogger;
    @Autowired
    private MyConverter myConverter;

    //    private static final String[] springBeans={"myDataSource","myUpdateMap","entityManagerFactory","transactionManager","myLogger"};
    public void tune() throws Exception
    {
//        context = new DefaultCamelContext();
//        context=(CamelContext)applicationContext.getBean("camelContext");
//        for(String s:springBeans) context.getRegistry().bind(s, applicationContext.getBean(s));
//         context.getTypeConverterRegistry().addTypeConverter(Incident.class,Map.class, (TypeConverter) applicationContext.getBean("myConverter"));

    camelContext.getTypeConverterRegistry().addTypeConverter(Incident.class,Map.class,myConverter);
/*         String[] beans=applicationContext.getBeanDefinitionNames();
        for(String s:beans) if(s.toUpperCase().contains("JPA"))
        {
            myLogger.getLogger().info(s+" "+
                    applicationContext.getBean(s).getClass().getCanonicalName());
        }*/
//        context.addRoutes(applicationContext.getBean(MyRouteBuilder.class));
//        context.start();
    }

    public static void main(String[] args) throws Exception {

        applicationContext=SpringApplication.run(MyRest.class, args);
        Thread.sleep(3000);
        ((MyRest)applicationContext.getBean("myRest")).tune();
    }
   @Bean
    public ServletRegistrationBean myCamelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        registration.setName("CamelServlet");
        return registration;
    }
}
