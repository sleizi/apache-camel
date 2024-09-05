package com.example.camel.camel_microservice_1.routes.patterns;

import com.example.camel.camel_microservice_1.routes.CurrencyExchange;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EipPatternsRouter extends RouteBuilder {

    SplitterComponent splitterComponent;
    DynamicRouterBean dynamicRouterBean;

    @Override
    public void configure() throws Exception {

        getContext().setTracing(true);

        errorHandler(deadLetterChannel("activemq:dead-letter-queue"));

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

//        from("file:files/csv")
//                .convertBodyTo(String.class)
////                .split(body(), ",")
//                .split(method(splitterComponent))
//                .to("activemq:split-queue");

        from("file:files/aggregate-json")
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .aggregate(simple("${body.to}"), new ArrayListAggregationStrategy())
                .completionSize(3)
//                .completionTimeout(HIGHEST)
                .to("log:aggregate-json");


        // routing slip
//        String routingSlip = "direct:endpoint1, direct:endpoint2";
//        from("timer:routingSlip?period=10000")
//                .transform().constant("My message is hard-coded.")
//                .routingSlip(simple(routingSlip));


        // Dynamic routing
        // Step 1, 2, 3
        from("timer:dynamicRouting?period={{time-period}}")
                .transform().constant("My message is hard-coded.")
                .dynamicRouter(method(dynamicRouterBean));



        from("direct:endpoint1")
                .wireTap("log:wire-tap")
                .to("{{endpoint-for-logging}}");
        from("direct:endpoint2")
                .to("log:directendpoint2");
    }

}

@Component
class SplitterComponent {
    public String[] split(String body) {
        return body.split(",");
    }
}

@Data
@NoArgsConstructor
class ArrayListAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Object newBody = newExchange.getIn().getBody();
        List<Object> list;
        if (oldExchange == null) {
            list = new ArrayList<>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }
    }
}

@Component
@Slf4j
class DynamicRouterBean {

    int invocations;

    public String decideTheNextEndpoint(@ExchangeProperties Map<String, String> properties,
                                        @Headers Map<String, String> headers,
                                        @Body String body) {
        log.info("{}", properties);
        log.info("{}", headers);
        log.info("{}", body);
        invocations++;
        if (invocations % 2 == 0)
            return "direct:endpoint1";
        else
            return "direct:endpoint2";
    }
}