package com.example.camel.camel_microservice_1.routes.a;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//@Component
public class MyFirstTimerRouter extends RouteBuilder {

    @Autowired
    GetCurrentTimeBean getCurrentTimeBean;
    @Autowired
    SimpleLoggingProcessingComponent simpleLoggingProcessingComponent;

    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .log("${body}")
                .transform().constant("My constant message")
                .log("${body}")
//                .transform().constant("Time now is: " + LocalDateTime.now())
//                .bean("getCurrentTimeBean")
                .bean(getCurrentTimeBean, "invokeThis")
                .log("${body}")
                .bean(simpleLoggingProcessingComponent)
                .log("${body}")
                .process(new SimpleLoggingProcessor())
                .to("log:first-timer");
    }
}

@Component
class GetCurrentTimeBean {
    public String getCurrentTime() {
        return "The time now is " + LocalDateTime.now();
    }

    public String invokeThis() {
        return "Time now is " + LocalDateTime.now();
    }
}

@Component
@Slf4j
class SimpleLoggingProcessingComponent {
    public void process(String message) {
        log.info("Simple logging {}", message);
    }
}

@Slf4j
class SimpleLoggingProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Simple logging processor {}", exchange.getMessage().getBody());
    }
}