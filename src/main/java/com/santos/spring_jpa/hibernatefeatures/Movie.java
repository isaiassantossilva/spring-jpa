package com.santos.spring_jpa.hibernatefeatures;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.ParamDef;

import java.math.BigDecimal;

/**
 * Recursos especificos do Hibernate (fora do padrao JPA):
 * - @NaturalId: chave de negocio imutavel com lookup otimizado;
 * - @Formula: atributo read-only calculado por SQL no SELECT;
 * - @FilterDef/@Filter: restricao parametrizada ligada por sessao.
 */
@FilterDef(name = "minPrice", parameters = @ParamDef(name = "min", type = BigDecimal.class))
@Filter(name = "minPrice", condition = "base_price >= :min")
@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@NaturalId
	@Column(name = "imdb_id", unique = true)
	private String imdbId;

	@Column(name = "base_price", precision = 10, scale = 2)
	private BigDecimal basePrice;

	/** Calculado pelo banco a cada SELECT; nao existe como coluna gravavel. */
	@Formula("base_price * 1.2")
	private BigDecimal priceWithTax;

	public Movie(String title, String imdbId, BigDecimal basePrice) {
		this.title = title;
		this.imdbId = imdbId;
		this.basePrice = basePrice;
	}
}
