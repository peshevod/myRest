package com.shugalev.myrest;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.ArrayList;

public class MyAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Incident newBody = newExchange.getIn().getBody(Incident.class);
        ArrayList<Incident> list = null;
        if (oldExchange == null) {
            list = new ArrayList<Incident>();
        } else {
            list = oldExchange.getIn().getBody(ArrayList.class);
        }
        list.add(newBody);
        newExchange.getIn().setBody(list);
        return newExchange;
    }
}
