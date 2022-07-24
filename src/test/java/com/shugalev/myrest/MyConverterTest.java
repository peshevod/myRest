package com.shugalev.myrest;

import com.google.gson.GsonBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
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

@CamelSpringBootTest
@SpringBootTest
class MyConverterTest {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;
    private static final String query1="\n" +
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
    @Test
    public void myConverter() throws InterruptedException {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(query1, mapType);
        MockEndpoint mockA = camelContext.getEndpoint("mock:a", MockEndpoint.class);

        // sending data to route consumer
        Incident inc = producerTemplate.requestBody("mock:a", map, Incident.class);
        assertNotNull(inc, "Incident==null");
        myLogger.getLogger().info("MyConverter before {}, after {}", map, inc);
        if(map.containsKey("id")) assertEquals(Long.parseLong(map.get("id")), inc.getId()," Id not equal");
        if(map.containsKey("status")) assertEquals(Integer.parseInt(map.get("status")), inc.getStatus()," Status not equal");
        if(map.containsKey("priority")) assertEquals(Integer.parseInt(map.get("priority")), inc.getPriority()," Priority not equal");
        if(map.containsKey("severity")) assertEquals(Integer.parseInt(map.get("severity")), inc.getSeverity()," Severity not equal");
        if(map.containsKey("category")) assertEquals(Integer.parseInt(map.get("category")), inc.getCategory()," Category not equal");
        if(map.containsKey("subject")) assertEquals(map.get("subject"), inc.getSubject()," Subject not equal");
        if(map.containsKey("description")) assertEquals(map.get("description"), inc.getDescription()," Description not equal");
        if(map.containsKey("assignee")) assertEquals(map.get("assignee"), inc.getAssignee()," Assignee not equal");
        if(map.containsKey("create_date")) assertEquals(map.get("create_date"), inc.getCreate_date()," Create_date not equal");
        if(map.containsKey("update_date")) assertEquals(map.get("update_date"), inc.getUpdate_date()," Update_date not equal");
        if(map.containsKey("start_date")) assertEquals(map.get("start_date"), inc.getStart_date()," Start_date not equal");
        if(map.containsKey("close_date")) assertEquals(map.get("close_date"), inc.getClose_date()," Close_date not equal");
    }
}