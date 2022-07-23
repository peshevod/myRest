package com.shugalev.myrest;

import com.google.gson.GsonBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.camel.model.dataformat.JsonLibrary.Gson;
import static org.junit.jupiter.api.Assertions.*;

//@CamelSpringBootTest
@SpringBootTest
class MyConverterTest {

    @Autowired
    private CamelContext context;

    private ProducerTemplate producerTemplate;
    private final String query1="[\n" +
            "    {\n" +
            "    \"id\":\"20\",\n" +
            "    \"status\":\"77\",\n" +
            "    \"subject\":\"Fire incident in room 13\",\n" +
            "    \"description\":\"Burning computers\",\n" +
            "    \"priority\":\"5\",\n" +
            "    \"severity\":\"3\",\n" +
            "    \"assignee\":\"Ivan Nikolaevich\",\n" +
            "    \"category\":\"18\"\n" +
            "    },\n" +
           "    {\"status\":\"9\",\n" +
            "    \"subject\":\"Fire incident in room 255\",\n" +
            "    \"description\":\"Burning computers\",\n" +
            "    \"priority\":\"7\",\n" +
            "    \"severity\":\"12\",\n" +
            "    \"assignee\":\"Ivan Nikolaevich\",\n" +
            "    \"category\":\"181\"\n" +
            "    }\n" +
            "]";
    private final String query2="\n" +
            "    {\n" +
            "    \"id\":\"20\",\n" +
            "    \"status\":\"77\",\n" +
            "    \"subject\":\"Fire incident in room 13\",\n" +
            "    \"description\":\"Burning computers\",\n" +
            "    \"priority\":\"5\",\n" +
            "    \"severity\":\"3\",\n" +
            "    \"assignee\":\"Ivan Nikolaevich\",\n" +
            "    \"category\":\"18\"\n" +
            "    }\n";

    @Autowired
    private MyLogger myLogger;
    Map<String, String> map;
    ArrayList<Map<String,String>> maps;
    @BeforeEach
    public void setup() {
        producerTemplate = context.createProducerTemplate();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Map<String,String>>>(){}.getType();
        Type mapType = new TypeToken<Map<String,String>>(){}.getType();
        maps = gson.fromJson(query1, listType);
        map=gson.fromJson(query2, mapType);
        myLogger.getLogger().info("List={}",maps);
        myLogger.getLogger().info("map={}",map);
    }
    @Test
    void toIncident() {
        final Incident inc1 = producerTemplate.requestBody("direct:create", maps, Incident.class);
        assertNotNull(inc1," Incident not converted");
        final Incident inc2 = producerTemplate.requestBody("direct:create", map, Incident.class);
        assertNotNull(inc2," Incident not converted");
    }
}