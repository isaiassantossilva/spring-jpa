package com.santos.spring_jpa.ids;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidade com chave primaria composta via @IdClass (dois campos @Id). */
@Entity
@Table(name = "country_languages")
@IdClass(CountryLanguageId.class)
@Getter
@Setter
@NoArgsConstructor
public class CountryLanguage {

	@Id
	private String country;

	@Id
	private String language;

	private boolean official;

	public CountryLanguage(String country, String language, boolean official) {
		this.country = country;
		this.language = language;
		this.official = official;
	}
}
