package com.santos.spring_jpa.ids;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Chave composta com @IdClass: classe simples (nao anotada com @Embeddable)
 * cujos campos espelham os campos @Id da entidade.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CountryLanguageId implements Serializable {

	private String country;
	private String language;
}
