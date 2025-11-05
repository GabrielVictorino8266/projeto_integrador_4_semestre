package com.projetoanderson.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.client.payment.PaymentClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class MercadoPagoConfig {

    @Value("${MERCADO_PAGO_ACCESS_TOKEN}")
    private String accessToken;

    @PostConstruct
    public void init() {
    	com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}