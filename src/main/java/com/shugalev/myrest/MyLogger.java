package com.shugalev.myrest;

import java.util.Map;

import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.*;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerContext;
//import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ilya
 */

/*
    Logger settings
    two types - org.slf4j and org.apache.log4j
*/


@ConfigurationProperties(prefix = "log")
@Configuration("myLogger")
public class MyLogger {
    
    private Map<String,String> myLogger;
    private org.apache.logging.log4j.Logger logger1;
    private org.apache.logging.log4j.Logger logger;
    
    private void configureMyLogger()
    {
//        logger= LoggerFactory.getLogger(MyLogger.class);
        logger= LogManager.getRootLogger();
        logger.info("This is {}","MyLogger");
        logger1= LogManager.getLogger(MyLogger.class);
        logger1.info("This is {}","MyLogger");
        ConsoleAppender con=ConsoleAppender
                .newBuilder()
                .setLayout(PatternLayout
                        .newBuilder()
                        .withPattern("%d{ISO8601}  %-5p[       %t](%F - %L)%c{1}  : %m%n")
                        .build())
                .setName("CONSOLE_ERR")
                .setTarget(Target.SYSTEM_OUT)
                .build();
        con.start();
        ((org.apache.logging.log4j.core.Logger)logger1).addAppender(con);
        if(myLogger!=null && !myLogger.isEmpty() && myLogger.containsKey("file"))
        {
            logger1.info(myLogger==null ? "null":"file="+myLogger.get("file2"));
            FileAppender f=FileAppender
                    .newBuilder()
                    .withFileName(myLogger.get("file2"))
                    .setName("FILE_ERR")
                    .setLayout(PatternLayout.newBuilder().withPattern("%d{ISO8601}  %-5p[       %t](%F - %L)%c{1}  : %m%n").build())
                    .setImmediateFlush(true)
                    .build();
            f.start();
            ((org.apache.logging.log4j.core.Logger)logger1).addAppender(f);
            logger1.info("Begin logging to File");
            if(!myLogger.containsKey("console") || !myLogger.get("console").equalsIgnoreCase("Always")) ((org.apache.logging.log4j.core.Logger)logger1).removeAppender(con);
        }
        else logger1.info(myLogger==null ? "myLogger=null":"file="+myLogger.get("file"));
    }
    
    public org.apache.logging.log4j.Logger getLogger()
    {
        return logger;
    }
    
    public org.apache.logging.log4j.Logger getLogger1()
    {
        return logger1;

    }
    
    public Map<String,String> getMyLogger()
    {
          return myLogger;      
    }
    
    public void setMyLogger(Map<String,String> myLogger)
    {
        this.myLogger=myLogger;
        configureMyLogger();
    }
}
