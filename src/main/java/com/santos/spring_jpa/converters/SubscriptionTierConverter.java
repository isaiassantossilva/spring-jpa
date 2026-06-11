package com.santos.spring_jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * AttributeConverter: controla como o atributo vira coluna. Aqui o enum e
 * gravado pelo codigo curto ("P") em vez do nome ("PRO") — mais robusto a
 * renomeacoes do enum do que @Enumerated(STRING) e mais legivel que ORDINAL.
 * autoApply = true aplica a todo atributo SubscriptionTier sem precisar de @Convert.
 */
@Converter(autoApply = true)
public class SubscriptionTierConverter implements AttributeConverter<SubscriptionTier, String> {

	@Override
	public String convertToDatabaseColumn(SubscriptionTier attribute) {
		return attribute == null ? null : attribute.getCode();
	}

	@Override
	public SubscriptionTier convertToEntityAttribute(String dbData) {
		return dbData == null ? null : SubscriptionTier.fromCode(dbData);
	}
}
