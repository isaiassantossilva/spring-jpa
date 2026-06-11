package com.santos.spring_jpa.relationships.onetoone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Lado dono do @OneToOne: a coluna de FK (owner_id) fica nesta tabela. */
@Entity
@Table(name = "passports")
@Getter
@Setter
@NoArgsConstructor
public class Passport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "passport_number")
	private String number;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", unique = true)
	private Person owner;

	public Passport(String number) {
		this.number = number;
	}
}
