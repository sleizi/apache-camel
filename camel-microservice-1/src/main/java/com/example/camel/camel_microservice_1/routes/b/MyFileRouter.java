package com.example.camel.camel_microservice_1.routes.b;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MyFileRouter extends RouteBuilder {

    DeciderBean deciderBean;

    @Override
    public void configure() throws Exception {

        from("file:files/input")
//                .pipeline()
                .routeId("Files-Input-Route")
                .transform().body(String.class, body -> body.toUpperCase())
                .choice()
                .when(simple("${file:ext} == 'xml'"))
                .log("XML FILE")
//                .when(simple("${body} contains 'USD'"))
//                .log("Not an XML FILE BUT contains USD")
                .when(method(deciderBean))
                .log("deciderBean condition met")
                .otherwise()
                .log("Not an XML FILE")
                .end()
//                .to("direct:log-file-values")
                .to("file:files/output");

        from("direct:log-file-values")
                .log("${messageHistory} ${headers.camelFileAbsolutePath}")
                .log("${file:name} ${file:name.ext} ${file:name.noext} ${file:onlyname}")
                .log("${file:onlyname.noext} ${file:parent} ${file:path} ${file:absolute}")
                .log("${file:size} ${file:modified}")
                .log("${routeId} ${camelId} ${body}");
    }
}

@Component
@Slf4j
class DeciderBean {
    public boolean isThisConditionMet(@Body String body,
                                      @Headers Map<String, String> headers,
                                      @ExchangeProperties Map<String, String> properties) {
        log.info("DeciderBean body {} ", body);
        log.info("DeciderBean headers {}", headers);
        log.info("DeciderBean properties {}", properties);
        return true;
    }
}
