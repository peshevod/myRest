package com.shugalev.myrest;

import java.util.Map;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
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
    private org.slf4j.Logger logger;
    private org.apache.log4j.Logger logger1;
    
    private void configureMyLogger()
    {
        logger= LoggerFactory.getLogger(MyLogger.class);
        logger1= LogManager.getLogger(MyLogger.class);
        ConsoleAppender con=new ConsoleAppender();
        con.setName("CONSOLE_ERR");
        con.setTarget("System.out");
        con.setLayout(new PatternLayout("%d{ISO8601}  %-5p[       %t]%c{1}  : %m%n"));
        con.activateOptions();
        logger1.addAppender(con);
        if(myLogger!=null && !myLogger.isEmpty() && myLogger.containsKey("file"))
        {
            logger.info(myLogger==null ? "null":"file="+myLogger.get("file"));
            FileAppender f=new FileAppender();
            f.setFile(myLogger.get("file"));
            f.setEncoding("utf-8");
            f.setName("FILE_ERR");
            f.setLayout(new PatternLayout("%d{ISO8601}  %-5p[       %t]%c{1}  : %m%n"));
            f.setImmediateFlush(true);
            f.activateOptions();
            logger1.addAppender(f);
            logger1.info("Begin logging to File");
            if(!myLogger.containsKey("console") || !myLogger.get("console").equalsIgnoreCase("Always")) logger1.removeAppender(con);
        }
        else logger1.info(myLogger==null ? "myLogger=null":"file="+myLogger.get("file"));
    }
    
    public org.slf4j.Logger getLogger()
    {
        return logger;
    }
    
    public org.apache.log4j.Logger getLogger1()
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
