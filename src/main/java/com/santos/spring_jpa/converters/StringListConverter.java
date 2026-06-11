package com.santos.spring_jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Converter de tipo composto: List<String> <-> CSV numa unica coluna.
 * Alternativa leve ao @ElementCollection quando nao se precisa consultar
 * os elementos individualmente. Sem autoApply: exige @Convert no atributo.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		return (attribute == null || attribute.isEmpty()) ? null : String.join(",", attribute);
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		return (dbData == null || dbData.isBlank())
				? new ArrayList<>()
				: new ArrayList<>(Arrays.asList(dbData.split(",")));
	}
}
