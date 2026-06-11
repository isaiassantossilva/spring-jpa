package com.santos.spring_jpa.inheritance.tableperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Heranca TABLE_PER_CLASS: a classe abstrata NAO tem tabela; cada subclasse
 * concreta tem a sua, repetindo as colunas herdadas. Consultas polimorficas
 * viram UNION ALL. IDENTITY nao e permitido — os IDs precisam ser unicos na
 * hierarquia inteira (aqui, sequence compartilhada via AUTO).
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
public abstract class Shape {

	@Id
	@GeneratedValue
	private Long id;

	private String color;

	protected Shape(String color) {
		this.color = color;
	}
}
