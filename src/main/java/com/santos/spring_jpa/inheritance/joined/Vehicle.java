package com.santos.spring_jpa.inheritance.joined;

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

/**
 * Heranca JOINED: uma tabela por classe, ligadas pela PK. Esquema
 * normalizado (colunas NOT NULL possiveis), ao custo de JOINs nas consultas.
 */
@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String brand;

	protected Vehicle(String brand) {
		this.brand = brand;
	}
}
