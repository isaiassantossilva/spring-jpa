package com.santos.spring_jpa.basics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Mapeamentos basicos: @Entity, @Table, @Id com IDENTITY, @Column
 * (nullable/length/unique/precision), @Enumerated, @Lob e @Transient.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(unique = true, length = 40)
	private String sku;

	@Column(precision = 12, scale = 2)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductStatus status = ProductStatus.DRAFT;

	@Lob
	private String description;

	@Column(name = "created_at", updatable = false)
	private Instant createdAt = Instant.now();

	/** Nao e persistido: calculado em memoria. */
	@Transient
	private BigDecimal priceWithTax;

	public Product(String name, String sku, BigDecimal price) {
		this.name = name;
		this.sku = sku;
		this.price = price;
	}
}
