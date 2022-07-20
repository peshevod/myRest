package com.shugalev.myrest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConverter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
//@RequestMapping(path="/camel")
public class MyRouteBuilder extends RouteBuilder
{
    @Autowired
    private ApplicationContext appContext;
//    @Autowired
//    private MyLogger myLogger;
    @Value("${log.update.address}")
    private String updateAddr;

/*   @Bean
//    private Incident myIncident()
    {
        return new Incident();
    }*/

    public void setUpdateAddress(String updateAddr)
    {
        this.updateAddr=updateAddr;
    }

    /*
        Database field variables. Need for sql request
    */

    private final String[] intgr={"ID","STATUS","PRIORITY","SEVERITY","CATEGORY",};
    private final String[] strng={"SUBJECT","DESCRIPTION","ASSIGNEE"};
    private final String[] tmstmp={"CREATE_DATE","UPDATE_DATE","START_DATE","CLOSE_DATE"};
    private final HashMap<String,Integer> types=new HashMap<>();
    private static final int TYPE_STRING=0;
    private static final int TYPE_INT=1;
    private static final int TYPE_TIMESTAMP=2;

    private String delete_query;

    private String makeSQL(int id,String sql1)
    {
        return makeSQL(id==-1 ? "":"ID="+id, sql1);
    }

    private String makeSQL(String query,String sql1)
    {
        String sql2="";
        if(query!=null && !query.isEmpty())
        {
            String[] queries=query.toUpperCase().split("[=&]");
            if(queries.length%2==0)
            {
                sql2+=" WHERE ";
                for(int i=0;i<queries.length;i+=2)
                {
                    if(types.containsKey(queries[i]))
                    {
                        if(types.get(queries[i])==TYPE_INT) sql2+=queries[i]+"="+queries[i+1]+" AND ";
                        else sql2+=queries[i]+"='"+queries[i+1]+"' AND ";
                    }
                    else
                    {
                        sql2="SELECT 'ERROR','WRONG QUERY "+query+"'";
                        break;
                    }
                }
                if(sql2.length()>7) sql2=sql2.substring(0,sql2.length()-5);
            } else sql2="SELECT 'ERROR','WRONG QUERY "+query+"'";
        }
        if(!sql2.startsWith("SELECT")) return sql1+sql2;
        else return sql2;
    }

    /*
        Convert to Uppercase MAP keys
    */

    private Map keysUp(Map<String,String> in)
    {
        HashMap<String,String> newmap=new HashMap<>();
        for(String s:in.keySet()) newmap.put(s.toUpperCase(), in.get(s));
        return newmap;
    }

    private List listKeysUp(List<Map<String,String>> in)
    {
        ArrayList<Map<String,String>> newlist=new ArrayList<>();
        for(Map<String,String> map:in) newlist.add(keysUp(map));
        return newlist;
    }

    @Override
    public void configure() throws Exception {


//        myLogger.getLogger().info("-----Start configure!!!");

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);
//                .component("jpa");

        /*
             GET requests parameters define filters. Without parameters - get all records
        */

        rest("/camel/")
                .get("/ilya/{id}")
                .to("direct:read")
                .get("/ilya")
                .to("direct:read")
                .post("/ilya")
                .to("direct:create")
                .put("/ilya")
                .to("direct:update")
                .delete("/ilya/{id}")
                .to("direct:delete")
                .delete("/ilya/all")
                .to("direct:deleteall");

        from("direct:read")
                .to("log:GET_IN?level=INFO&showBody=true&showHeaders=true")
/*                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
//                       String query=(String)exchange.getIn().getHeader("CamelHttpQuery");
                        String sql1="SELECT * FROM MYSCHEMA.INCIDENTS";
//                       exchange.getIn().setBody(makeSQL(query,sql1));
                        exchange.getIn().setBody(makeSQL(exchange.getIn().getHeader("id",Integer.class),sql1));
                    }
                })*/
                .to("log:GET_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
//                .to("jdbc:myDataSource")
               .choice()
                    .when(header("id").isNotNull())
                        .toD("jpa:Incident?query=select o from Incident o where o.id = ${header.id}")
                    .otherwise()
                        .to("jpa:Incident?query=select o from Incident o")
                .end()
                .to("log:GET_AFTER_DB?level=INFO&showBody=true&showHeaders=true");


//        rest()
//                .endRest();


//        rest()
//                .endRest();


        from("direct:create")
                // Prepare SQL query
                .to("log:POST_IN?level=INFO&showBody=true&showHeaders=true")
                .split(body()).aggregationStrategy(new MyAggregationStrategy())
                .to("log:AFTER_SPLIT?level=INFO&showBody=true&showHeaders=true")
//                .marshal(new JacksonDataFormat(Incident.class))
//                .to("log:POST_BEFORE_UNMARSHALL?level=INFO&showBody=true&showHeaders=true")
//                .bean("myIncident","setFromMap")
//                .unmarshal(new JacksonDataFormat())
//                .marshal().json(JsonLibrary.Jackson, Incident.class)
                .convertBodyTo(Incident.class)
                .setBody(body().convertTo(Incident.class).method("clearId"))
//                .to("log:POST_AFTER_UNMARSHALL?level=INFO&showBody=true&showHeaders=true")
 /*               .process(new Processor() {
                 @Override
                 public void process(Exchange exchange) throws Exception {
                   Map<String,String> map=keysUp((Map)exchange.getIn().getBody(Map.class));
                   String sql1="INSERT INTO MYSCHEMA.INCIDENTS (STATUS,SUBJECT,DESCRIPTION,PRIORITY,SEVERITY,ASSIGNEE,CATEGORY,CREATE_DATE,UPDATE_DATE) VALUES ";
                       sql1+="("
                           +map.get("STATUS")+",'"+map.get("SUBJECT")+"','"+map.get("DESCRIPTION")+"',"+map.get("PRIORITY")+","
                           +map.get("SEVERITY")+",'"+map.get("ASSIGNEE")+"',"+map.get("CATEGORY")
                           +",CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP())";
                   exchange.getIn().setBody(sql1);
               }
               })*/
                .to("log:POST_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
                .to("jpa:Incident")
//                .to("jdbc:myDataSource")
                // Return the id of created incident
                .to("log:POST_AFTER_DB?level=INFO&showBody=true&showHeaders=true")
/*                .process(new Processor(){
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String sql1="SELECT MAX(ID) AS ID FROM MYSCHEMA.INCIDENTS";
                        exchange.getIn().setBody(sql1);
                    }
                })
                .to("jdbc:myDataSource")*/
//                .to("jpa:Incident")
//              .aggregate(body())
                .to("log:POST_AFTER_DB_DOP?level=INFO&showBody=true&showHeaders=true");


//        rest()
//                .endRest();


//       rest()
//                .endRest();


        from("direct:update")
                .to("log:PUT_IN_ilya?level=INFO&showBody=true&showHeaders=true")
                .setProperty("InputMap",body())
                .convertBodyTo(Incident.class)
                // Create SQL query
/*                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Map<String,String> map=keysUp((HashMap) exchange.getIn().getBody(Map.class));
                        String sql1="UPDATE MYSCHEMA.INCIDENTS SET UPDATE_DATE=CURRENT_TIMESTAMP()";
                        String sql0="";
                        if(map.containsKey("ID"))
                        {
                            for(String s:map.keySet())
                            {
                                if(types.containsKey(s))
                                {
                                    if(!s.equalsIgnoreCase("ID"))
                                    {
                                        switch(types.get(s))
                                        {
                                            case TYPE_INT:
                                                sql0+=","+s+'='+map.get(s);
                                                break;
                                            case TYPE_STRING:
                                            case TYPE_TIMESTAMP:
                                                sql0+=","+s+"='"+map.get(s)+"'";
                                        }
                                    }
                                }
                                else
                                {
                                    sql1="SELECT 'ERROR','WRONG Field "+s+"'";
                                    break;
                                }
                            }
                            if(!sql0.startsWith("SELECT"))
                            {
                                sql1+=sql0+" WHERE ID="+map.get("ID");
                            }
                        }
                        else sql1="SELECT 'ERROR','No id Field'";
                        exchange.getIn().setBody(sql1);
                        //Prepare e-mail fields
                        exchange.getIn().setHeader("subject","Incident "+map.get("ID")+" update");
                        exchange.getIn().setHeader("messageToAdmin","Incident id="+map.get("ID")+" updated"+sql0);
                    }
                })*/
                .to("log:PUT_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
//                .to("jdbc:myDataSource")
                .setHeader("id",body().convertTo(Incident.class).method("getId"))
                .to("log:PUT_AFTER_GET_ID?level=INFO&showBody=true&showHeaders=true")
                .toD("jpa:Incident?query=select o from Incident o where id=${header.id}")
                .to("log:PUT_AFTER_QUERY?level=INFO&showBody=true&showHeaders=true")
                .setBody(body().convertTo(Incident.class).method("update(${InputMap})"))
                .to("log:PUT_AFTER_UPDATE?level=INFO&showBody=true&showHeaders=true")
/*                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("to",updateAddr);
                        exchange.getIn().setHeader("Content-Type", "text/plain");
                        exchange.getIn().setBody("Attention to Administrator!\n\t"+exchange.getIn().getHeader("messageToAdmin"));
                    }
                })*/
                // Send E-mail
//                .to("smtp://{{mail.smtp.host}}:{{mail.smtp.port}}?username={{mail.smtp.username}}&password={{mail.smtp.password}}{{mail.smtp.options}}")
                .to("log:PUT_AFTER_EMAIL?level=INFO&showBody=true&showHeaders=true");

//        rest()

        from("direct:deleteall")
                .setHeader("id", simple("-1"))
                .to("direct:delete");


        from("direct:delete")
                // Get Record for writing in log
                .to("log:DELETE_IN_ilya1?level=INFO&showBody=true&showHeaders=true")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
//                       String query=(String)exchange.getIn().getHeader("CamelHttpQuery");
                        String sql1="SELECT * FROM MYSCHEMA.INCIDENTS";
//                       exchange.getIn().setBody(makeSQL(query,sql1));
                        exchange.getIn().setBody(makeSQL(exchange.getIn().getHeader("id",Integer.class),sql1));
                    }
                })
                .to("log:GET_BEFORE_DELETE?level=INFO&showBody=true&showHeaders=true")
                .to("jdbc:myDataSource")
                .to("log:GET_COMPLETE_BEFORE_DELETE?level=INFO&showBody=true&showHeaders=true")
                //Prepare SQL for deletting
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        ArrayList<HashMap<String,String>> list=(ArrayList) exchange.getIn().getBody(ArrayList.class);
//                        String query=(String)exchange.getIn().getHeader("CamelHttpQuery");
                        String sql1="DELETE FROM MYSCHEMA.INCIDENTS";
//                        exchange.getIn().setBody(makeSQL(query,sql1));
                        exchange.getIn().setBody(makeSQL(exchange.getIn().getHeader("id",Integer.class),sql1));
                        exchange.getIn().setHeader("DeleteInfo", list.toString());
                    }
                })
                // Log for deletting. Deleted incident in DeleteInfo header
                .to("log:DELETE_INCIDENT?level=INFO&showBody=true&showHeaders=true")
                .to("jdbc:myDataSource");

//        CamelContext context = new DefaultCamelContext();
//        context.start();
//       context.getTypeConverterRegistry().addTypeConverter(com.shugalev.myrest.Incident.class, java.util.LinkedHashMap.class, new MyConverter());
    }

// Construct map for database fields

    public MyRouteBuilder()
    {
        for(String s:intgr) types.put(s, TYPE_INT);
        for(String s:strng) types.put(s, TYPE_STRING);
        for(String s:tmstmp)types.put(s,TYPE_TIMESTAMP);
    }

}
