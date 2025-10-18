package com.projetoanderson.app.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.context.annotation.Description;

import com.projetoanderson.app.dto.VeiculoRequestDTO;

import java.time.Year;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private VeiculoRequestDTO createValidDTO() {
        VeiculoRequestDTO dto = new VeiculoRequestDTO();
        dto.setNumeroVeiculo("ABC123");
        dto.setPlaca("ABC1234");
        dto.setTipoVeiculo("CARRO");
        dto.setMarca("MARCA");
        dto.setAnoFabricacao(Year.now().getValue());
        dto.setKmAtual(0);
        dto.setLimiteAvisoKm(10000);
        return dto;
    }

    // ==========================
    // numeroVeiculo
    // ==========================

    @Test
    void testNumeroVeiculo_QuandoValorValido_DevePassar() {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setNumeroVeiculo("ABC123");
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testNumeroVeiculo_QuandoTamanhoMaximo_DevePassar() {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setNumeroVeiculo("1234567890"); // exatamente 10 caracteres
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("numeroVeiculoInvalido")
    @Description("Deve falhar quando o numero do veículo for inválido")
    void testNumeroVeiculo_QuandoInvalido_DeveFalhar(String valor, String descricao) {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setNumeroVeiculo(valor);
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    private static Stream<Arguments> numeroVeiculoInvalido() {
        return Stream.of(
                Arguments.of(null, "null não permitido"),
                Arguments.of("", "string vazia"),
                Arguments.of("   ", "apenas espaços"),
                Arguments.of("12345678901", "acima de 10 caracteres")
        );
    }

    // ==========================
    // placa
    // ==========================

    @Test
    void testPlaca_QuandoFormatoAntigoValido_DevePassar() {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setPlaca("ABC1234"); // formato antigo
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testPlaca_QuandoFormatoMercosulValido_DevePassar() {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setPlaca("ABC1A23"); // formato Mercosul
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("placaInvalida")
    @Description("Deve falhar quando a placa for inválida")
    void testPlaca_QuandoInvalido_DeveFalhar(String valor, String descricao) {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setPlaca(valor);
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    private static Stream<Arguments> placaInvalida() {
        return Stream.of(
                Arguments.of(null, "null não permitido"),
                Arguments.of("", "string vazia"),
                Arguments.of("abc1234", "letras minúsculas"),
                Arguments.of("ABCDEFG12", "mais de 8 caracteres"),
                Arguments.of("AB123", "menos de 7 caracteres"),
                Arguments.of("123ABCD", "formato inválido para regex")
        );
    }

    // ==========================
    // tipoVeiculo
    // ==========================

    @Test
    void testTipoVeiculo_QuandoValorValidoEnum_DevePassar() {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setTipoVeiculo("CARRO"); // valor válido do enum
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("tipoVeiculoInvalido")
    @Description("Deve falhar quando o tipo de veículo for inválido")
    void testTipoVeiculo_QuandoInvalido_DeveFalhar(String valor, String descricao) {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setTipoVeiculo(valor);
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    private static Stream<Arguments> tipoVeiculoInvalido() {
        return Stream.of(
                Arguments.of(null, "null não permitido"),
                Arguments.of("", "string vazia"),
                Arguments.of("valor inválido", "valor inválido do enum")
        );
    }

    // ==========================
    // anoFabricacao
    // ==========================

    @Test
    void testAnoFabricacao_QuandoAnoMinimo_DevePassar() {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setAnoFabricacao(1900); // valor mínimo aceito
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testAnoFabricacao_QuandoAnoAtual_DevePassar() {
        int anoAtual = Year.now().getValue();
        VeiculoRequestDTO dto = createValidDTO();
        dto.setAnoFabricacao(anoAtual); // ano atual
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("anoFabricacaoInvalido")
    @Description("Deve falhar quando o ano de fabricação for inválido")
    void testAnoFabricacao_QuandoInvalido_DeveFalhar(Integer valor, String descricao) {
        VeiculoRequestDTO dto = createValidDTO();
        dto.setAnoFabricacao(valor);
        Set<ConstraintViolation<VeiculoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    private static Stream<Arguments> anoFabricacaoInvalido() {
        int anoAtual = Year.now().getValue();
        return Stream.of(
                Arguments.of(1899, "ano menor que 1900"),
                Arguments.of(anoAtual + 1, "ano maior que o atual")
        );
    }
}
