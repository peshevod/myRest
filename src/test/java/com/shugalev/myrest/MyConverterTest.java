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

}