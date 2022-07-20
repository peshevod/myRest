package com.shugalev.myrest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MyLoggerTest {
    @Autowired
    private MyLogger myLogger;
    @Test
    void getLogger() {
        if(myLogger==null) System.out.println("logger=null");
    }

    @Test
    void getLogger1() {
    }

    @Test
    void getMyLogger() {
    }

    @Test
    void setMyLogger() {
    }
}