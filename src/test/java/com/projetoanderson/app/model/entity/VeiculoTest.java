package com.projetoanderson.app.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

@DisplayName("Testes da Entidade Veiculo")
class VeiculoTest {

    private Validator validator;

    @BeforeEach
    void configurarTeste() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Veiculo criarVeiculoValido() {
        Veiculo v = new Veiculo();
        v.setNumeroVeiculo("V001");
        v.setPlaca("ABC1D23");
        v.setTipoVeiculo(TipoVeiculo.CARRO);
        v.setAnoFabricacao(2020);
        v.setMarca("Toyota");
        v.setKmAtual(50000);
        v.setLimiteAvisoKm(80000);
        v.setCriadoPor("admin");
        return v;
    }

    private void assertSemViolacoes(Veiculo v) {
        Set<ConstraintViolation<Veiculo>> violations = validator.validate(v);
        assertTrue(violations.isEmpty(),
                () -> "Esperava nenhum erro de validação, mas ocorreu: " + violations);
    }

    private void assertViolacao(Veiculo v, String campoEsperado) {
        Set<ConstraintViolation<Veiculo>> violations = validator.validate(v);
        assertFalse(violations.isEmpty(), "Esperava pelo menos uma violação");
        assertTrue(violations.stream().anyMatch(vio ->
                        vio.getPropertyPath().toString().equals(campoEsperado)),
                () -> "Esperava violação no campo: " + campoEsperado);
    }

    @Test @DisplayName("Deve criar veículo com valores válidos")
    void devecriarVeiculoComValoresValidos() {
        assertSemViolacoes(criarVeiculoValido());
    }

    @Test @DisplayName("Deve converter placa para maiúsculo")
    void deveConverterPlacaParaMaiusculo() {
        Veiculo v = criarVeiculoValido();
        v.setPlaca("abc1234");
        assertEquals("ABC1234", v.getPlaca());
    }

    @Test @DisplayName("Deve inicializar com status ATIVO por padrão")
    void deveInicializarComStatusAtivoPorPadrao() {
        Veiculo v = new Veiculo();
        assertEquals(StatusVeiculo.ATIVO, v.getStatus());
    }

    @Test @DisplayName("Deve inicializar com data de criação")
    void deveInicializarComDataDeCriacao() {
        Veiculo v = new Veiculo();
        assertNotNull(v.getCriadoEm());
    }

    @Test @DisplayName("Deve falhar com número do veículo vazio")
    void deveFalharComNumeroDoVeiculoVazio() {
        Veiculo v = criarVeiculoValido();
        v.setNumeroVeiculo("");
        assertViolacao(v, "numeroVeiculo");
    }

    @Test @DisplayName("Deve falhar com placa inválida")
    void deveFalharComPlacaInvalida() {
        Veiculo v = criarVeiculoValido();
        v.setPlaca("123INVALIDA");
        assertViolacao(v, "placa");
    }

    @Test @DisplayName("Deve falhar com ano de fabricação muito antigo")
    void deveFalharComAnoDeFabricacaoMuitoAntigo() {
        Veiculo v = criarVeiculoValido();
        v.setAnoFabricacao(1800);
        assertViolacao(v, "anoFabricacao");
    }

    @Test @DisplayName("Deve falhar com KM atual negativo")
    void deveFalharComKmAtualNegativo() {
        Veiculo v = criarVeiculoValido();
        v.setKmAtual(-10);
        assertViolacao(v, "kmAtual");
    }

    @Test @DisplayName("Deve aceitar placas no formato Mercosul")
    void deveAceitarPlacasNoFormatoMercosul() {
        Veiculo v = criarVeiculoValido();
        v.setPlaca("BRA2E19");
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve aceitar placas no formato antigo")
    void deveAceitarPlacasNoFormatoAntigo() {
        Veiculo v = criarVeiculoValido();
        v.setPlaca("ABC1234");
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve definir e obter campos de auditoria corretamente")
    void deveDefinirEObterCamposDeAuditoriaCorretamente() {
        Veiculo v = criarVeiculoValido();
        LocalDateTime agora = LocalDateTime.now();
        v.setCriadoPor("teste");
        v.setCriadoEm(agora);
        v.setAtualizadoPor("teste2");
        v.setAtualizadoEm(agora);

        assertEquals("teste", v.getCriadoPor());
        assertEquals(agora, v.getCriadoEm());
        assertEquals("teste2", v.getAtualizadoPor());
        assertEquals(agora, v.getAtualizadoEm());
    }

    @Test @DisplayName("Deve alterar status do veículo")
    void deveAlterarStatusDoVeiculo() {
        Veiculo v = criarVeiculoValido();
        v.setStatus(StatusVeiculo.INDISPONIVEL);
        assertEquals(StatusVeiculo.INDISPONIVEL, v.getStatus());
    }

    @Test @DisplayName("Deve aceitar KM atual zero")
    void deveAceitarKmAtualZero() {
        Veiculo v = criarVeiculoValido();
        v.setKmAtual(0);
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve falhar com limite aviso KM negativo")
    void deveFalharComLimiteAvisoKmNegativo() {
        Veiculo v = criarVeiculoValido();
        v.setLimiteAvisoKm(-1);
        assertViolacao(v, "limiteAvisoKm");
    }

    @Test @DisplayName("Deve aceitar limite aviso KM zero")
    void deveAceitarLimiteAvisoKmZero() {
        Veiculo v = criarVeiculoValido();
        v.setLimiteAvisoKm(0);
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve aceitar ano de fabricação mínimo (1900)")
    void deveAceitarAnoFabricacaoMinimo() {
        Veiculo v = criarVeiculoValido();
        v.setAnoFabricacao(1900);
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve aceitar ano de fabricação atual")
    void deveAceitarAnoFabricacaoAtual() {
        Veiculo v = criarVeiculoValido();
        v.setAnoFabricacao(Year.now().getValue());
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve falhar com marca muito longa")
    void deveFalharComMarcaMuitoLonga() {
        Veiculo v = criarVeiculoValido();
        v.setMarca("X".repeat(21));
        assertViolacao(v, "marca");
    }

    @Test @DisplayName("Deve aceitar marca com tamanho máximo")
    void deveAceitarMarcaComTamanhoMaximo() {
        Veiculo v = criarVeiculoValido();
        v.setMarca("X".repeat(20));
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve falhar com número veículo muito longo")
    void deveFalharComNumeroVeiculoMuitoLongo() {
        Veiculo v = criarVeiculoValido();
        v.setNumeroVeiculo("X".repeat(11));
        assertViolacao(v, "numeroVeiculo");
    }

    @Test @DisplayName("Deve aceitar número veículo com tamanho máximo")
    void deveAceitarNumeroVeiculoComTamanhoMaximo() {
        Veiculo v = criarVeiculoValido();
        v.setNumeroVeiculo("X".repeat(10));
        assertSemViolacoes(v);
    }

    @Test @DisplayName("Deve falhar com todos os campos obrigatórios nulos")
    void deveFalharComTodosCamposObrigatoriosNulos() {
        Veiculo v = new Veiculo();
        Set<ConstraintViolation<Veiculo>> violations = validator.validate(v);
        assertFalse(violations.isEmpty());
    }

    @Test @DisplayName("Deve falhar com tipo veículo nulo")
    void deveFalharComTipoVeiculoNulo() {
        Veiculo v = criarVeiculoValido();
        v.setTipoVeiculo(null);
        assertViolacao(v, "tipoVeiculo");
    }

    @Test @DisplayName("Deve aceitar diferentes tipos de veículo")
    void deveAceitarDiferentesTiposDeVeiculo() {
        Veiculo v = criarVeiculoValido();
        for (TipoVeiculo tipo : TipoVeiculo.values()) {
            v.setTipoVeiculo(tipo);
            assertSemViolacoes(v);
        }
    }
}
