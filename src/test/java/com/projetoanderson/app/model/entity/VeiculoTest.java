//package com.projetoanderson.app.model.entity;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.Instant;
//import java.util.Set;
//import java.util.stream.Stream;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
//import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
//import jdk.jfr.Description;
//
//class VeiculoTest {
//
//    private static Validator validator;
//
//    @BeforeAll
//    static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    private Veiculo createValidVeiculo() {
//        Veiculo veiculo = new Veiculo();
//        veiculo.setNumeroVeiculo("V001");
//        veiculo.setPlaca("ABC1234");
//        veiculo.setTipoVeiculo(TipoVeiculo.CAMINHAO);
//        veiculo.setAnoFabricacao(2020);
//        veiculo.setMarca("Volvo");
//        veiculo.setKmAtual(50000);
//        veiculo.setLimiteAvisoKm(80000);
//        veiculo.setStatus(StatusVeiculo.ATIVO);
//        return veiculo;
//    }
//
//    // ==========================
//    // numeroVeiculo
//    // ==========================
//
//    @Test
//    void testNumeroVeiculo_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setNumeroVeiculo("ABC123");
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testNumeroVeiculo_QuandoTamanhoMaximo_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setNumeroVeiculo("1234567890");
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @ParameterizedTest(name = "{1}")
//    @MethodSource("numeroVeiculoInvalido")
//    @Description("Deve falhar quando o numero do veículo for inválido")
//    void testNumeroVeiculo_QuandoInvalido_DeveFalhar(String valor, String descricao) {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setNumeroVeiculo(valor);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    private static Stream<Arguments> numeroVeiculoInvalido() {
//        return Stream.of(
//                Arguments.of(null, "null não permitido"),
//                Arguments.of("", "string vazia"),
//                Arguments.of("   ", "apenas espaços"),
//                Arguments.of("12345678901", "acima de 10 caracteres")
//        );
//    }
//
//    // ==========================
//    // placa
//    // ==========================
//
//    @Test
//    void testPlaca_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setPlaca("XYZ9876");
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testPlaca_QuandoTamanhoMaximo_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setPlaca("ABC12345");
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testPlaca_QuandoMinuscula_DeveConverterParaMaiuscula() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setPlaca("abc1234");
//        assertThat(veiculo.getPlaca()).isEqualTo("ABC1234");
//    }
//
//    @ParameterizedTest(name = "{1}")
//    @MethodSource("placaInvalida")
//    @Description("Deve falhar quando a placa for inválida")
//    void testPlaca_QuandoInvalida_DeveFalhar(String valor, String descricao) {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setPlaca(valor);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    private static Stream<Arguments> placaInvalida() {
//        return Stream.of(
//                Arguments.of(null, "null não permitido"),
//                Arguments.of("", "string vazia"),
//                Arguments.of("   ", "apenas espaços"),
//                Arguments.of("ABC123456", "acima de 8 caracteres")
//        );
//    }
//
//    // ==========================
//    // tipoVeiculo
//    // ==========================
//
//    @Test
//    void testTipoVeiculo_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setTipoVeiculo(TipoVeiculo.CAMINHAO);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testTipoVeiculo_QuandoNull_DeveFalhar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setTipoVeiculo(null);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    // ==========================
//    // anoFabricacao
//    // ==========================
//
//    @Test
//    void testAnoFabricacao_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setAnoFabricacao(2023);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testAnoFabricacao_QuandoNull_DeveFalhar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setAnoFabricacao(null);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    // ==========================
//    // marca
//    // ==========================
//
//    @Test
//    void testMarca_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setMarca("Mercedes");
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testMarca_QuandoTamanhoMaximo_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setMarca("12345678901234567890");
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @ParameterizedTest(name = "{1}")
//    @MethodSource("marcaInvalida")
//    @Description("Deve falhar quando a marca for inválida")
//    void testMarca_QuandoInvalida_DeveFalhar(String valor, String descricao) {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setMarca(valor);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    private static Stream<Arguments> marcaInvalida() {
//        return Stream.of(
//                Arguments.of(null, "null não permitido"),
//                Arguments.of("", "string vazia"),
//                Arguments.of("   ", "apenas espaços"),
//                Arguments.of("123456789012345678901", "acima de 20 caracteres")
//        );
//    }
//
//    // ==========================
//    // kmAtual
//    // ==========================
//
//    @Test
//    void testKmAtual_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setKmAtual(100000);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testKmAtual_QuandoNull_DeveFalhar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setKmAtual(null);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    // ==========================
//    // limiteAvisoKm
//    // ==========================
//
//    @Test
//    void testLimiteAvisoKm_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setLimiteAvisoKm(150000);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testLimiteAvisoKm_QuandoNull_DeveFalhar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setLimiteAvisoKm(null);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    // ==========================
//    // status
//    // ==========================
//
//    @Test
//    void testStatus_QuandoValorValido_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setStatus(StatusVeiculo.INDISPONIVEL);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//
//    @Test
//    void testStatus_QuandoNull_DeveFalhar() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setStatus(null);
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isNotEmpty();
//    }
//
//    @Test
//    void testStatus_QuandoNaoDefinido_DeveSerAtivo() {
//        Veiculo veiculo = new Veiculo();
//        assertThat(veiculo.getStatus()).isEqualTo(StatusVeiculo.ATIVO);
//    }
//
//    // ==========================
//    // Campos de auditoria
//    // ==========================
//
//    @Test
//    void testCriadoEm_QuandoNaoDefinido_DeveSerDataAtual() {
//        Veiculo veiculo = new Veiculo();
//        assertThat(veiculo.getCriadoEm()).isNotNull();
//        assertThat(veiculo.getCriadoEm()).isBefore(Instant.now().plusSeconds(1));
//    }
//
//    @Test
//    void testCriadoPor_QuandoDefinido_DeveRetornarValor() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setCriadoPor("admin");
//        assertThat(veiculo.getCriadoPor()).isEqualTo("admin");
//    }
//
//    @Test
//    void testAtualizadoEm_QuandoDefinido_DeveRetornarValor() {
//        Veiculo veiculo = createValidVeiculo();
//        Instant agora = Instant.now();
//        veiculo.setAtualizadoEm(agora);
//        assertThat(veiculo.getAtualizadoEm()).isEqualTo(agora);
//    }
//
//    @Test
//    void testAtualizadoPor_QuandoDefinido_DeveRetornarValor() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setAtualizadoPor("user123");
//        assertThat(veiculo.getAtualizadoPor()).isEqualTo("user123");
//    }
//
//    @Test
//    void testExcluidoPor_QuandoDefinido_DeveRetornarValor() {
//        Veiculo veiculo = createValidVeiculo();
//        veiculo.setExcluidoPor("admin");
//        assertThat(veiculo.getExcluidoPor()).isEqualTo("admin");
//    }
//
//    // ==========================
//    // Validação completa
//    // ==========================
//
//    @Test
//    void testVeiculo_QuandoTodosCamposValidos_DevePassar() {
//        Veiculo veiculo = createValidVeiculo();
//        Set<ConstraintViolation<Veiculo>> violations = validator.validate(veiculo);
//        assertThat(violations).isEmpty();
//    }
//}