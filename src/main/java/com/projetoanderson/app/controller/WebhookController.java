package com.projetoanderson.app.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.service.PagamentoService;

@RestController
@RequestMapping("/api/public/webhooks") // Endpoint público (conforme SecurityConfig)
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final PagamentoService pagamentoService;

    public WebhookController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receberNotificacaoMP(@RequestBody Map<String, Object> notificacao,
                                                     @RequestParam(required = false) String topic) {
        
        logger.info("Webhook Mercado Pago recebido (Topic: {}): {}", topic, notificacao);

        try {
            Long paymentId = null;

            if (notificacao.containsKey("type") && "payment".equals(notificacao.get("type"))) {
                Map<String, Object> data = (Map<String, Object>) notificacao.get("data");
                if (data != null && data.containsKey("id")) {
                    paymentId = Long.parseLong(data.get("id").toString());
                }
            } 

            if (paymentId != null) {
                 pagamentoService.processarConfirmacaoPagamento(paymentId);
            } else {
                logger.warn("Não foi possível extrair o 'payment_id' da notificação.");
            }

        } catch (Exception e) {
            logger.error("Erro ao processar webhook do Mercado Pago: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }
}