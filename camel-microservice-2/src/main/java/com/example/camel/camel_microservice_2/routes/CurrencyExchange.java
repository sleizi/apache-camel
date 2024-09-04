package com.example.camel.camel_microservice_2.routes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CurrencyExchange implements Serializable {
    Long id;
    String from;
    String to;
    BigDecimal conversionMultiple;
}
