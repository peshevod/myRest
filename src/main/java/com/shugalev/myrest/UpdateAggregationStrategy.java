package com.shugalev.myrest;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.List;
import java.util.Map;

public class UpdateAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (newExchange == null || oldExchange==null) return null;
        List<Incident> incs=oldExchange.getIn().getBody(List.class);
        if(incs!=null) for(Incident inc:incs)
        {
            Map map=newExchange.getIn().getBody(Map.class);
            if(map!=null) inc.update(map);
            oldExchange.getIn().setBody(inc,Incident.class);
            return oldExchange;
        }
        return null;
    }
}
