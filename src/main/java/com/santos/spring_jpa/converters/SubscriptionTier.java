package com.santos.spring_jpa.converters;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum SubscriptionTier {
	FREE("F"),
	PRO("P"),
	ENTERPRISE("E");

	private final String code;

	SubscriptionTier(String code) {
		this.code = code;
	}

	public static SubscriptionTier fromCode(String code) {
		return Stream.of(values())
				.filter(t -> t.code.equals(code))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Codigo desconhecido: " + code));
	}
}
