package com.shugalev.myrest;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@CamelSpringBootTest
@SpringBootTest
class MyRouteBuilderTest {
    @Autowired
    private CamelContext camelContext;
    private ProducerTemplate producerTemplate;
    private final String query1="\n" +
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

    private final String query2="[\n" +
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
    @Autowired
    private MyLogger myLogger;
    @BeforeEach
    public void setup() {
        producerTemplate = camelContext.createProducerTemplate();
    }
    @Test
    void configure(){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Map<String,String>>>(){}.getType();
        Type mapType = new TypeToken<Map<String,String>>(){}.getType();
        Map<String, String>map=gson.fromJson(query1, mapType);
        ArrayList<Map<String,String>>maps = gson.fromJson(query2, listType);
        myLogger.getLogger().info("map={}",map);
        myLogger.getLogger().info("List={}",maps);
        /*
        *       Create and read test
        */
        /*      Only one create */
        final Incident inc1 = producerTemplate.requestBody("direct:create", map, Incident.class);
        assertNotNull(inc1," Incident not converted");
        final ArrayList<Incident> inc3 = (ArrayList<Incident>) producerTemplate.requestBodyAndHeader("direct:read",null,"id",inc1.getId().toString());
        assertEquals(inc3.get(0).getId(),inc1.getId());
        /*      Multiple create */
        final ArrayList<Incident> inc2 = producerTemplate.requestBody("direct:create", maps, ArrayList.class);
        assertNotNull(inc2," Incident not converted");
        final ArrayList<Incident> inc4 = (ArrayList<Incident>) producerTemplate.requestBody("direct:read", null,ArrayList.class);
        assertEquals(inc4.get(inc4.size()-1).getId(),inc2.get(inc2.size()-1).getId());
        /*
         *       Update and read test
         */
        /*      Only one update */
        String new_description1="New_description1";
        LinkedHashMap<String,String> umap1=new LinkedHashMap<>();
        umap1.put("id",inc4.get(inc4.size()-1).getId().toString());
        umap1.put("description",new_description1);
        assertNotEquals(new_description1,inc4.get(inc4.size()-1).getDescription(), "New Description1 already set");
        final ArrayList<Incident> inc5 = (ArrayList<Incident>) producerTemplate.requestBody("direct:update", umap1, ArrayList.class);
        assertNotNull(inc5," Update not succesfull");
        final ArrayList<Incident> inc6 = (ArrayList<Incident>) producerTemplate.requestBodyAndHeader("direct:read",null,"id",inc4.get(inc4.size()-1).getId());
        assertEquals(new_description1,inc6.get(0).getDescription());
        /*      Multiple update */
        final String new_description2="New_description2";
        final String new_description3="New_description3";
        umap1.put("id",inc4.get(inc4.size()-2).getId().toString());
        umap1.put("description",new_description2);
        LinkedHashMap<String,String> umap2=new LinkedHashMap<>();
        umap2.put("id",inc4.get(inc4.size()-3).getId().toString());
        umap2.put("description",new_description3);
        ArrayList<LinkedHashMap<String,String>> ulist=new ArrayList<>();
        ulist.add(umap1);
        ulist.add(umap2);
        assertNotEquals(new_description2,inc4.get(inc4.size()-2).getDescription(), "New Description2 already set");
        assertNotEquals(new_description3,inc4.get(inc4.size()-3).getDescription(), "New Description3 already set");
        final ArrayList<Incident> inc7 = (ArrayList<Incident>) producerTemplate.requestBody("direct:update", ulist,ArrayList.class);
        assertNotNull(inc7," Update not succesfull");
        final ArrayList<Incident> inc8 = (ArrayList<Incident>) producerTemplate.requestBodyAndHeader("direct:read",null,"id",inc4.get(inc4.size()-2).getId());
        assertNotNull(inc8," Update not succesfull");
        assertEquals(new_description2,inc8.get(0).getDescription());
        final ArrayList<Incident> inc9 = (ArrayList<Incident>) producerTemplate.requestBodyAndHeader("direct:read",null,"id",inc4.get(inc4.size()-3).getId());
        assertNotNull(inc9," Update not succesfull");
        assertEquals(new_description3,inc9.get(0).getDescription());
        /*
         *       Delete and read test
         */
        final int inc10 = (int)producerTemplate.requestBodyAndHeader("direct:delete", null,"id", inc4.get(inc4.size()-3).getId().toString() );
        assertEquals(1,inc10, "Delete Not successfull");
        final ArrayList<Incident> inc11 = (ArrayList<Incident>) producerTemplate.requestBodyAndHeader("direct:read",null,"id",inc4.get(inc4.size()-3).getId());
        assertNotNull(inc11," Get after delete not succesfull");
        assertEquals(0,inc11.size(),"Not delete 1 row");
        /*
         *       Delete all and read all test
         */
        final int inc12 = (int)producerTemplate.requestBody("direct:deleteall", null,Integer.class);
        assertTrue(inc12>=2, "Delete all Not successfull");
        final ArrayList<Incident> inc13 = (ArrayList<Incident>) producerTemplate.requestBody("direct:read", null,ArrayList.class);
        assertEquals(0,inc13.size(), "Not all records deleted");

    }
}