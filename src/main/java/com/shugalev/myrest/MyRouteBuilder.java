package com.shugalev.myrest;

/**
 *
 * @author ilya
 */
import java.util.ArrayList;

import com.sun.jdi.IntegerValue;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.apache.camel.CamelContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;



@Component
public class MyRouteBuilder extends RouteBuilder
{
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private DataSourceProperties dataSourceProperties;
    @Autowired
    private MyLogger myLogger;
    @Value("${log.update.address}")
    private String updateAddr;
    
    public void setUpdateAddress(String updateAddr)
    {
        this.updateAddr=updateAddr;
    }

    /*
        Database field variables. Need for sql request
    */
    
    private static final String[] intgr={"ID","STATUS","PRIORITY","SEVERITY","CATEGORY",};
    private static final String[] strng={"SUBJECT","DESCRIPTION","ASSIGNEE"};
    private static final String[] tmstmp={"CREATE_DATE","UPDATE_DATE","START_DATE","CLOSE_DATE"};
    private final HashMap<String,Integer> types=new HashMap<>();
    private static final int TYPE_STRING=0;
    private static final int TYPE_INT=1;
    private static final int TYPE_TIMESTAMP=2;
    
    private String delete_query;
    
    /*
        Datasource bean for access to Database
    */
    
    @Bean
    public DataSource myDataSource()
    {
//        dataSourceProperties.logProperties();
        BasicDataSource ds=new BasicDataSource();
        ds.setDriverClassName(dataSourceProperties.getDriver());
        ds.setUsername(dataSourceProperties.getUsername());
        ds.setPassword(dataSourceProperties.getPassword());
        ds.setUrl(dataSourceProperties.getConnectURI());
        return ds;
    }
    
    /*
        Procedure of construcing WHERE clause fo SQL requests from HTTP request parameters
    */
    
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

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        /*
             GET requests parameters define filters. Without parameters - get all records
        */
        rest()
                .get("/ilya/{id}")
                .route()
                .to("direct:read")
                .endRest();
        rest()
                .get("/ilya")
                .route()
                .setHeader("id",simple("-1"))
                .to("direct:read")
                .endRest();

        /*
             Endpoint to process get requests
        */

        from("direct:read")
                .to("log:GET_IN?level=INFO&showBody=true&showHeaders=true")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
//                       String query=(String)exchange.getIn().getHeader("CamelHttpQuery");
                       String sql1="SELECT * FROM MYSCHEMA.INCIDENTS";
//                       exchange.getIn().setBody(makeSQL(query,sql1));
                        exchange.getIn().setBody(makeSQL(exchange.getIn().getHeader("id",Integer.class),sql1));
                   }
                })
                .to("log:GET_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
                .to("jdbc:myDataSource")
                .to("log:GET_AFTER_DB?level=INFO&showBody=true&showHeaders=true");
        
        /*
             POST - Create incidents. JSON format in body id field mandatory
        */
        
        rest()
                .post("/ilya")
                .route()
                .to("log:POST_IN?level=INFO&showBody=true&showHeaders=true")
                .to("direct:create")
                .endRest();
        
        /* 
            One more POST with another path. Example how to make multiple entries
        */
        
        rest()
                .post("/ilya1")
                .route()
                .to("log:POST_IN?level=INFO&showBody=true&showHeaders=true")
                .to("direct:create")
                .endRest();
        
        /*
            Processing POST - create records in incidents
        */

        from("direct:create")
                // Prepare SQL query
                .split(body()).aggregationStrategy(new MyAggregationStrategy())
                .process(new Processor() {
                 @Override
                 public void process(Exchange exchange) throws Exception {
                   List<Map<String,String>> list=listKeysUp((List) exchange.getIn().getBody(List.class));
                   int listSize=list.size();
                   String sql1="INSERT INTO MYSCHEMA.INCIDENTS (STATUS,SUBJECT,DESCRIPTION,PRIORITY,SEVERITY,ASSIGNEE,CATEGORY,CREATE_DATE,UPDATE_DATE) VALUES ";
                   for(int i=0;i<listSize;i++)
                       sql1+=(i==0?"":",")+"("
                           +list.get(i).get("STATUS")+",'"+list.get(i).get("SUBJECT")+"','"+list.get(i).get("DESCRIPTION")+"',"+list.get(i).get("PRIORITY")+","
                           +list.get(i).get("SEVERITY")+",'"+list.get(i).get("ASSIGNEE")+"',"+list.get(i).get("CATEGORY")
                           +",CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP())";
                   exchange.getIn().setBody(sql1);
               }
               })
                .to("log:POST_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
                .to("jdbc:myDataSource")
                // Return the id of created incident
                .to("log:POST_AFTER_DB?level=INFO&showBody=true&showHeaders=true")
                .process(new Processor(){
                     @Override
                     public void process(Exchange exchange) throws Exception {
                        String sql1="SELECT MAX(ID) AS ID FROM MYSCHEMA.INCIDENTS";
                        exchange.getIn().setBody(sql1);
                    }
                })
                .to("jdbc:myDataSource")
//              .aggregate(body())
                .to("log:POST_AFTER_DB_DOP?level=INFO&showBody=true&showHeaders=true");
        
        /*
             POST - Update incidents. JSON format in body id field mandatory
        */
        
        rest()
                .put("/ilya")
                .route()
                .to("log:PUT_IN_ilya?level=INFO&showBody=true&showHeaders=true")
                .to("direct:update")
                .endRest();
        
        /*
             POST - Update incidents. One more entry poin with another path. JSON format in body id field mandatory
        */
        
       rest()
                .put("/ilya1")
                .route()
                .to("log:PUT_IN_ilya1?level=INFO&showBody=true&showHeaders=true")
                .to("direct:update")
                .endRest();
        
        /*
             PUT processing
        */
        
        from("direct:update")
                // Create SQL query
                .process(new Processor() {
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
                })
                .to("log:PUT_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
                .to("jdbc:myDataSource")
                .to("log:PUT_BEFORE_EMAIL?level=INFO&showBody=true&showHeaders=true")
                .process(new Processor() {
                 @Override
                     public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("to",updateAddr);
                        exchange.getIn().setHeader("Content-Type", "text/plain");
                        exchange.getIn().setBody("Attention to Administrator!\n\t"+exchange.getIn().getHeader("messageToAdmin"));
                    }
                })
                // Send E-mail 
                .to("smtp://{{mail.smtp.host}}?port={{mail.smtp.port}}&username={{mail.smtp.username}}&password={{mail.smtp.password}}{{mail.smtp.options}}")
                .to("log:PUT_AFTER_EMAIL?level=INFO&showBody=true&showHeaders=true");
       
        /*
             DELETE requests parameters(as in GET) define filters. Without parameters - delete all records
        */
        rest()
                .delete("/ilya/{id}")
                .route()
                .to("direct:delete")
                .endRest();
        rest()
                .delete("/ilya/all")
                .route()
                .setHeader("id", simple("-1"))
                .to("direct:delete")
                .endRest();

        /*
            DELETE Processing
        */
        
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

        CamelContext context = new DefaultCamelContext();
        

        String[] beans=appContext.getBeanDefinitionNames();
        for(String s:beans) if(s.toUpperCase().contains("LOG"))
        {
            myLogger.getLogger1().info(s+" "+
            appContext.getBean(s).getClass().getCanonicalName());
        }


    }
    
// Construct map for database fields
    
    public MyRouteBuilder()
    {
        for(String s:intgr) types.put(s, TYPE_INT);
        for(String s:strng) types.put(s, TYPE_STRING);
        for(String s:tmstmp)types.put(s,TYPE_TIMESTAMP);
    }

}