package com.santos.spring_jpa.hibernatefeatures;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * @SQLRestriction: um WHERE fixo aplicado a TODA consulta da entidade.
 * Cupons expirados continuam na tabela, mas ficam invisiveis para o JPA.
 */
@Entity
@Table(name = "coupons")
@SQLRestriction("expired = false")
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;

	private boolean expired;

	public Coupon(String code) {
		this.code = code;
	}
}
