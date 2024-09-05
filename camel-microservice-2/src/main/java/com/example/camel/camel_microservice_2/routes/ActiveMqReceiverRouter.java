package com.example.camel.camel_microservice_2.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder {

    @Autowired
    MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;
    @Autowired
    MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

    @Override
    public void configure() throws Exception {
        from("activemq:my-activemq-queue")
                .unmarshal(createEncryptor())
//                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
//                .bean(myCurrencyExchangeProcessor)
//                .bean(myCurrencyExchangeTransformer)
                .to("log:received-message-from-active-mq");

//        from("activemq:my-activemq-xml-queue")
//                .unmarshal()
//                .jacksonXml(CurrencyExchange.class)
//                .to("log:received-message-from-active-mq");

//        from("activemq:split-queue")
//                .to("log:received-message-from-active-mq");
    }

    private CryptoDataFormat createEncryptor() throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        ClassLoader classLoader = getClass().getClassLoader();
        keyStore.load(classLoader.getResourceAsStream("myDesKey.jceks"), "someKeystorePassword".toCharArray());
        Key sharedKey = keyStore.getKey("myDesKey", "someKeyPassword".toCharArray());

        return new CryptoDataFormat("DES", sharedKey);
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

