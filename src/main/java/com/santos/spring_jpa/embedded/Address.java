package com.santos.spring_jpa.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Objeto de valor reutilizavel: vive na mesma tabela da entidade dona. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	private String street;
	private String city;
	private String zipCode;
}
