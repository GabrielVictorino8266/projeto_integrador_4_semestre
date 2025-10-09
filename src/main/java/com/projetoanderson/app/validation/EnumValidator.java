package com.projetoanderson.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collections;

public class EnumValidator implements ConstraintValidator<ValidEnum, CharSequence> {

    private List<String> acceptedValues = Collections.emptyList();
    private boolean ignoreCase = false;

    @Override
    public void initialize(ValidEnum annotation) {
        this.ignoreCase = annotation.ignoreCase();
        // Obtém os valores do enum especificado na anotação
        Enum<?>[] enumConstants = annotation.enumClass().getEnumConstants();
        if (enumConstants != null) {
            acceptedValues = Stream.of(enumConstants)
                                   .map(Enum::name)
                                   .collect(Collectors.toList());
        }
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // valor nulo é válido
        }
        String valueStr = value.toString();

        // Verifica se o valor é válido, considerando a opção de ignorar maiúsculas/minúsculas
        boolean valid = acceptedValues.stream()
                                    .anyMatch(acceptedValue -> ignoreCase ? acceptedValue.equalsIgnoreCase(valueStr) : acceptedValue.equals(valueStr));

        if (!valid) {
            // Desabilita a mensagem de violação padrão
            context.disableDefaultConstraintViolation();

            // Constrói uma mensagem com os valores aceitos
            String acceptedValuesString = String.join(", ", acceptedValues);

            // Adiciona a violação com a mensagem dinâmica incluindo os valores aceitos
            context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate().replace("{enumValues}", acceptedValuesString)
            ).addConstraintViolation();
        }

        return valid;
    }
}
