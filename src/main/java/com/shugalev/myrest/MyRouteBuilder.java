package com.shugalev.myrest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyRouteBuilder extends RouteBuilder
{
    @Autowired
    private MyLogger myLogger;
    @Value("${log.update.address}")
    private String updateAddr;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

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
                .to("log:GET_BEFORE_CHOICE?level=INFO&showBody=true&showHeaders=true")
                .choice()
                    .when(header("id").isNotNull())
                        .toD("jpa:Incident?query=select o from Incident o where o.id = ${header.id}")
                    .otherwise()
                        .to("jpa:Incident?query=select o from Incident o")
                .end()
                .to("log:GET_AFTER_CHOICE?level=INFO&showBody=true&showHeaders=true");

        from("direct:create")
                .to("log:POST_IN?level=INFO&showBody=true&showHeaders=true")
                .choice()
                    .when(body().isInstanceOf(List.class))
                        .to("direct:createsplit")
                    .otherwise()
                        .to("direct:createcontinue")
                .end();

        from("direct:createsplit")
                .split(body()).aggregationStrategy(new MyAggregationStrategy())
                .to("log:AFTER_SPLIT?level=INFO&showBody=true&showHeaders=true")
                .to("direct:createnonsplit");

        from("direct:createcontinue")
                .convertBodyTo(Incident.class)
                .setBody(body().convertTo(Incident.class).method("clearId"))
                .to("log:POST_BEFORE_DB?level=INFO&showBody=true&showHeaders=true")
                .to("jpa:Incident")
                .to("log:POST_AFTER_DB?level=INFO&showBody=true&showHeaders=true");

        from("direct:update")
                .choice()
                    .when(body().isInstanceOf(List.class))
                        .to("direct:updatesplit")
                    .otherwise()
                        .to("direct:updatecontinue")
                .end();
        from("direct:updatesplit")
                .split(body()).aggregationStrategy(new MyAggregationStrategy())
                .to("log:UPDATE_AFTER_SPLIT?level=INFO&showBody=true&showHeaders=true")
                .to("direct:updatecontinue");

        from("direct:updatecontinue")
                .to("log:PUT_IN_ilya?level=INFO&showBody=true&showHeaders=true")
                .recipientList(simple("direct:map,direct:updateIncident"));

        from("direct:map")
                .to("log:DIRECT_MAP?level=INFO&showBody=true&showHeaders=true")
                .to("bean:myUpdateMap?method=setMap(${bodyAs(java.util.LinkedHashMap)})");

        from("direct:updateIncident")
                .to("log:PUT_BEFORE_GET_ID?level=INFO&showBody=true&showHeaders=true")
                .setHeader("id",simple("${body[id]}"))
                .to("log:PUT_AFTER_GET_ID?level=INFO&showBody=true&showHeaders=true")
                .toD("jpa:Incident?query=select o from Incident o where o.id=${header.id}")
                .to("log:PUT_AFTER_QUERY?level=INFO&showBody=true&showHeaders=true")
                .enrich("bean:myUpdateMap?method=getMap()",new UpdateAggregationStrategy())
                .to("log:PUT_AFTER_UPDATE?level=INFO&showBody=true&showHeaders=true")
//                .to("smtp://{{mail.smtp.host}}:{{mail.smtp.port}}?username={{mail.smtp.username}}&password={{mail.smtp.password}}{{mail.smtp.options}}")
                .to("log:PUT_AFTER_EMAIL?level=INFO&showBody=true&showHeaders=true")
                .to("jpa:Incident");

        from("direct:deleteall")
                .to("jpa:Incident?query=delete from Incident&useExecuteUpdate=true");
        from("direct:delete")
                .to("log:DELETE_IN_ilya?level=INFO&showBody=true&showHeaders=true")
                .toD("jpa:Incident?query=delete from Incident o where o.id=${header.id}&useExecuteUpdate=true")
                .to("log:AFTER_DELETE?level=INFO&showBody=true&showHeaders=true");
    }
}
