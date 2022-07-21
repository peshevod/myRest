package com.shugalev.myrest;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyUpdateMap {
    private Map map;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

}
