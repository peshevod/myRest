package com.shugalev.myrest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Suite
class MyLoggerTest {
    @Autowired
    private MyLogger myLogger;
    @Test
    void configureMyLogger() {
        assertNotNull(myLogger, "myLogger=null");
        assertNotNull(myLogger.getLogger(),"getLogger() is null");
        assertNotNull(myLogger.getLogger1(),"getLogger1() is null");
        assertNotNull(myLogger.getMyLogger(),"Map is null");
        assertNotNull(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger())).getAppenders(),"logger No appenders");
        assertNotNull(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger1())).getAppenders(),"logger1 No appenders");
        assertNotEquals(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger())).getAppenders().size(),0);
        assertNotEquals(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger1())).getAppenders().size(),0);
        assertNotNull(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger())).getAppenders().get("Console"), "logger No console appender");
        assertNotNull(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger())).getAppenders().get("ApplicationFileLog"), "logger No File appender");
        assertNotNull(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger1())).getAppenders().get("CONSOLE_ERR"), "logger1 No console appender");
        assertNotNull(((org.apache.logging.log4j.core.Logger)(myLogger.getLogger1())).getAppenders().get("FILE_ERR"), "logger1 No File appender");
    }
}