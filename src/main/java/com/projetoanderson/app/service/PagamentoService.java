package com.projetoanderson.app.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.projetoanderson.app.dto.PagamentoPixRequestDTO;
import com.projetoanderson.app.dto.PagamentoPixResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.PedidoPagamento;
import com.projetoanderson.app.model.entity.enums.TipoPlano;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.PedidoPagamentoRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;

@Service
public class PagamentoService {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoService.class);

    private final PaymentClient paymentClient;
    private final PedidoPagamentoRepository pedidoPagamentoRepository;
    private final EmpresaRepository empresaRepository;

    public PagamentoService(PaymentClient paymentClient,
                            PedidoPagamentoRepository pedidoPagamentoRepository,
                            EmpresaRepository empresaRepository) {
        this.paymentClient = paymentClient;
        this.pedidoPagamentoRepository = pedidoPagamentoRepository;
        this.empresaRepository = empresaRepository;
    }

    private Empresa getEmpresaDoUsuarioLogado() {
        UsuarioAuthenticated usuarioAuth = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return empresaRepository.findById(usuarioAuth.getUsuario().getEmpresa().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa do usuário não encontrada."));
    }

    @Transactional
    public PagamentoPixResponseDTO criarPagamentoPix(PagamentoPixRequestDTO dto) {

        Empresa empresa = getEmpresaDoUsuarioLogado();

        PedidoPagamento pedido = new PedidoPagamento();
        pedido.setEmpresa(empresa);
        pedido.setValor(dto.getTransactionAmount());
        pedido.setStatus("PENDENTE");
        pedido.setAtivo(true);
        pedido = pedidoPagamentoRepository.save(pedido);

        try {
            OffsetDateTime expirationTime = OffsetDateTime.now(ZoneOffset.of("-03:00")).plusMinutes(30);

            PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                    .email(dto.getEmail())
                    .identification(IdentificationRequest.builder()
                            .type(dto.getIdentificationType())
                            .number(dto.getIdentificationNumber())
                            .build())
                    .build();

            PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                    .transactionAmount(dto.getTransactionAmount())
                    .description(dto.getDescription())
                    .paymentMethodId("pix")
                    .payer(payerRequest)
                    .dateOfExpiration(expirationTime)
                    .externalReference(pedido.getId().toString()) // Linka ao nosso Pedido ID
                    .build();

            logger.info("Criando pagamento Pix para o pedido interno ID: {}", pedido.getId());
            Payment payment = paymentClient.create(createRequest);

            if (payment == null || payment.getId() == null) {
                 throw new MPException("Resposta inválida da API do Mercado Pago ao criar pagamento.");
            }

            pedido.setMercadoPagoPaymentId(payment.getId());
            pedidoPagamentoRepository.save(pedido);

            String qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
            String qrCode = payment.getPointOfInteraction().getTransactionData().getQrCode();

            logger.info("Pagamento Pix (MP ID: {}) criado com sucesso.", payment.getId());
            return new PagamentoPixResponseDTO(payment.getId(), payment.getStatus(), qrCodeBase64, qrCode);

        } catch (MPApiException e) {
            logger.error("Erro da API do Mercado Pago: {}", e.getApiResponse().getContent(), e);
            pedido.setStatus("FALHA");
            pedidoPagamentoRepository.save(pedido);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro da API do Mercado Pago: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            logger.error("Erro no SDK do Mercado Pago: {}", e.getMessage(), e);
            pedido.setStatus("FALHA");
            pedidoPagamentoRepository.save(pedido);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro no SDK do Mercado Pago: " + e.getMessage());
        }
    }

    @Transactional
    public void processarConfirmacaoPagamento(Long mercadoPagoPaymentId) {
        logger.info("Processando confirmação para o MP Payment ID: {}", mercadoPagoPaymentId);

        Payment payment;
        try {
            payment = paymentClient.get(mercadoPagoPaymentId);
            if (payment == null) {
                logger.warn("Pagamento {} não encontrado na API do MP.", mercadoPagoPaymentId);
                return;
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar pagamento {} na API do MP: {}", mercadoPagoPaymentId, e.getMessage());
            return;
        }

        PedidoPagamento pedido = pedidoPagamentoRepository.findByMercadoPagoPaymentId(mercadoPagoPaymentId)
                .orElse(null);

        if (pedido == null) {
            logger.warn("PedidoPagamento interno não encontrado para o MP Payment ID: {}", mercadoPagoPaymentId);
            return; // Pedido não é nosso, ignora
        }

        if ("approved".equals(payment.getStatus()) && !"APROVADO".equals(pedido.getStatus())) {

            Empresa empresa = pedido.getEmpresa();
            empresa.setTipoPlano(TipoPlano.PAGO);
            empresaRepository.save(empresa);

            pedido.setStatus("APROVADO");
            pedidoPagamentoRepository.save(pedido);

            logger.info("SUCESSO: Plano PAGO ativado para a Empresa ID: {}", empresa.getId());

        } else if (("cancelled".equals(payment.getStatus()) || "rejected".equals(payment.getStatus())) 
                    && !"CANCELADO".equals(pedido.getStatus()) && !"REJEITADO".equals(pedido.getStatus())) {
            
            pedido.setStatus(payment.getStatus().toUpperCase()); // CANCELLED ou REJECTED
            pedidoPagamentoRepository.save(pedido);
            logger.info("Pagamento {} foi recusado ou cancelado.", mercadoPagoPaymentId);

        } else if ("pending".equals(payment.getStatus())){
             logger.info("Status do pagamento {} ainda é: PENDENTE", mercadoPagoPaymentId);
        } else {
             logger.info("Status do pagamento {} ({}) já processado ou ignorado.", mercadoPagoPaymentId, payment.getStatus());
        }
    }
}