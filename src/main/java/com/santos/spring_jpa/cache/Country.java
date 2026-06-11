package com.santos.spring_jpa.cache;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Cache de segundo nivel: compartilhado entre EntityManagers/transacoes, no
 * nivel do EntityManagerFactory. @Cacheable (JPA) opta a entidade no cache
 * (shared-cache-mode ENABLE_SELECTIVE) e @Cache (Hibernate) define a
 * estrategia de concorrencia e a regiao.
 *
 * Bom candidato: dado lido com frequencia e raramente alterado.
 */
@Entity
@Table(name = "countries")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "countries")
@Getter
@Setter
@NoArgsConstructor
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public Country(String name) {
		this.name = name;
	}
}
