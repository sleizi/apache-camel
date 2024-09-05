package com.example.camel.camel_microservice_2.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder {

    @Autowired
    MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;
    @Autowired
    MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

    @Override
    public void configure() throws Exception {
//        from("activemq:my-activemq-queue")
//                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
//                .bean(myCurrencyExchangeProcessor)
//                .bean(myCurrencyExchangeTransformer)
//                .to("log:received-message-from-active-mq");

//        from("activemq:my-activemq-xml-queue")
//                .unmarshal()
//                .jacksonXml(CurrencyExchange.class)
//                .to("log:received-message-from-active-mq");

        from("activemq:split-queue")
                .to("log:received-message-from-active-mq");
    }
}

@Component
@Slf4j
class MyCurrencyExchangeProcessor {

    public void process(CurrencyExchange currencyExchange) {
        log.info("Proceed message from Active MQ to get conversionMultiple: " + currencyExchange.getConversionMultiple());
    }

}


@Component
@Slf4j
class MyCurrencyExchangeTransformer {

    public void CurrencyExchange(CurrencyExchange currencyExchange) {
        currencyExchange.setConversionMultiple(
                currencyExchange.getConversionMultiple().multiply(new BigDecimal("10"))
        );
    }

}

