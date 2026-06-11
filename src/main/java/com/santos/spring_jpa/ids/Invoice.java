package com.santos.spring_jpa.ids;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Geracao de ID com SEQUENCE e @SequenceGenerator customizado. */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_seq")
	@SequenceGenerator(name = "invoice_seq", sequenceName = "invoice_sequence", initialValue = 1000, allocationSize = 10)
	private Long id;

	private BigDecimal total;

	public Invoice(BigDecimal total) {
		this.total = total;
	}
}
