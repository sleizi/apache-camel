package com.example.camel.camel_microservice_1.routes.patterns;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EipPatternsRouter extends RouteBuilder {

    SplitterComponent splitterComponent;

    @Override
    public void configure() throws Exception {
        // Pipeline
        // Content based routing - choice
        // Multicast

//        from("timer:multicast?period=10000")
//                .multicast()
//                .to("log:one", "log:two", "log:three");

//        from("file:files/csv")
//                .unmarshal().csv()
//                .split(body())
//                .to("activemq:split-queue");

        from("file:files/csv")
                .convertBodyTo(String.class)
//                .split(body(), ",")
                .split(method(splitterComponent))
                .to("activemq:split-queue");
    }
}

@Component
class SplitterComponent {
    public String[] split(String body) {
        return body.split(",");
    }
}