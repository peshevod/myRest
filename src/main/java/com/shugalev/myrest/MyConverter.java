package com.shugalev.myrest;

import org.apache.camel.Exchange;
import org.apache.camel.Converter;
import org.apache.camel.TypeConversionException;
import org.apache.camel.TypeConverters;
import org.apache.camel.support.TypeConverterSupport;
import org.springframework.stereotype.Component;

//import javax.persistence.Converter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;

@Component
public class MyConverter extends TypeConverterSupport {
    private static Map keysUp(Map<String,String> in)
    {
        HashMap<String,String> newmap=new HashMap<>();
        for(String s:in.keySet()) newmap.put(s.toUpperCase(), in.get(s));
        return newmap;
    }
    @Override
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        if (value.getClass().equals(java.util.LinkedHashMap.class)) {
            Map<String,String> map=keysUp((Map)value);
            return (T)(new Incident(map.containsKey("ID") ? Long.parseLong(map.get("ID")):null,
                    map.containsKey("STATUS")?Integer.parseInt(map.get("STATUS")):null,
                    map.containsKey("SUBJECT")?map.get("SUBJECT"):null,
                    map.containsKey("DESCRIPTION")?map.get("DESCRIPTION"):null,
                    map.containsKey("PRIORITY")?Integer.parseInt(map.get("PRIORITY")):null,
                    map.containsKey("SEVERITY")?Integer.parseInt(map.get("SEVERITY")):null,
                    map.containsKey("ASSIGNEE")?map.get("ASSIGNEE"):null,
                    map.containsKey("CATEGORY")?Integer.parseInt(map.get("CATEGORY")):null,
                    map.containsKey("CREATE_DATE")?Timestamp.valueOf(map.get("CREATE_DATE")):Timestamp.valueOf(now()),
                    map.containsKey("UPDATE_DATE")?Timestamp.valueOf(map.get("UPDATE_DATE")):Timestamp.valueOf(now()),
                    null, null));
        }
        return null;
    }
}


/*@Converter(generateLoader = true)
public class MyConverter implements TypeConverters {

    private static Map keysUp(Map<String,String> in)
    {
        HashMap<String,String> newmap=new HashMap<>();
        for(String s:in.keySet()) newmap.put(s.toUpperCase(), in.get(s));
        return newmap;
    }

    @Converter
    public Incident toIncident(LinkedHashMap data, Exchange exchange) {
        Map<String,String> map=keysUp(data);
        return new Incident(null, Integer.parseInt(map.get("STATUS")), map.get("SUBJECT"), map.get("DESCRIPTION"),
                Integer.parseInt(map.get("PRIORITY")), Integer.parseInt(map.get("SEVERITY")), map.get("ASSIGNEE"),
                Integer.parseInt(map.get("CATEGORY")), Timestamp.valueOf(now()), Timestamp.valueOf(now()), null, null);
    }
}*/
