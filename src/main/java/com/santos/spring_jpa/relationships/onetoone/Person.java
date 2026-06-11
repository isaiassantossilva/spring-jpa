package com.santos.spring_jpa.relationships.onetoone;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @OneToOne bidirecional. Este e o lado inverso (mappedBy); a FK fica na
 * tabela de Passport. cascade = ALL propaga persist/remove e
 * orphanRemoval remove o passaporte ao desvincular.
 */
@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Passport passport;

	public Person(String name) {
		this.name = name;
	}

	/** Metodo auxiliar para manter os dois lados sincronizados. */
	public void setPassportLinked(Passport passport) {
		if (passport == null && this.passport != null) {
			this.passport.setOwner(null);
		}
		if (passport != null) {
			passport.setOwner(this);
		}
		this.passport = passport;
	}
}
