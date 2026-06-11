package com.santos.spring_jpa.inheritance.singletable;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Heranca SINGLE_TABLE (padrao): todas as subclasses na mesma tabela,
 * diferenciadas pela coluna discriminadora. Mais rapida, mas as colunas
 * especificas das subclasses precisam ser nullable.
 */
@Entity
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal amount;

	protected Payment(BigDecimal amount) {
		this.amount = amount;
	}
}
