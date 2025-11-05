package com.projetoanderson.app.repository;

import com.projetoanderson.app.model.entity.PedidoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PedidoPagamentoRepository extends JpaRepository<PedidoPagamento, Long> {
    Optional<PedidoPagamento> findByMercadoPagoPaymentId(Long mercadoPagoPaymentId);
}