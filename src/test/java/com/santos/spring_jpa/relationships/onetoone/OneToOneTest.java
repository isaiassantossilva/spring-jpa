package com.santos.spring_jpa.relationships.onetoone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** @OneToOne bidirecional com cascade e orphanRemoval. */
@DataJpaTest
class OneToOneTest {

	@Autowired
	private PersonRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("cascade = ALL persiste o passaporte junto com a pessoa")
	void cascadePersist() {
		Person person = new Person("Maria");
		person.setPassportLinked(new Passport("BR123456"));

		Person saved = repository.save(person);
		em.flush();

		assertThat(saved.getPassport().getId()).isNotNull();
		assertThat(saved.getPassport().getOwner()).isSameAs(saved);
	}

	@Test
	@DisplayName("query method navega no relacionamento (findByPassportNumber)")
	void findByPassportNumber() {
		Person person = new Person("Carlos");
		person.setPassportLinked(new Passport("BR999999"));
		repository.saveAndFlush(person);
		em.clear();

		assertThat(repository.findByPassportNumber("BR999999"))
				.isPresent()
				.get().extracting(Person::getName).isEqualTo("Carlos");
	}

	@Test
	@DisplayName("orphanRemoval deleta o passaporte ao desvincular")
	void orphanRemoval() {
		Person person = new Person("Ana");
		person.setPassportLinked(new Passport("BR777777"));
		repository.saveAndFlush(person);

		person.setPassportLinked(null);
		em.flush();

		Long passports = em.getEntityManager()
				.createQuery("select count(p) from Passport p", Long.class)
				.getSingleResult();
		assertThat(passports).isZero();
	}
}
