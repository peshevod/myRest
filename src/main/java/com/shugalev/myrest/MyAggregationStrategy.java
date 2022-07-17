package com.shugalev.myrest;

import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.ArrayList;

public class MyAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        ArrayList newBody = newExchange.getIn().getBody(ArrayList.class);
        ArrayList<Object> list = null;
        if (oldExchange == null) {
            list = new ArrayList<Object>();
            list.addAll(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            list = oldExchange.getIn().getBody(ArrayList.class);
            list.addAll(newBody);
            return oldExchange;
        }
    }
}
