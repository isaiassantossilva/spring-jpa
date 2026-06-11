package com.santos.spring_jpa.embedded;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** @Embeddable/@Embedded e @AttributeOverrides. */
@DataJpaTest
class EmbeddedMappingTest {

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("embeddable e persistido nas colunas da tabela da entidade dona")
	void persistsEmbeddedValues() {
		Customer saved = em.persistFlushFind(new Customer("Joana",
				new Address("Rua A, 10", "Sao Paulo", "01000-000"),
				new Address("Av. B, 99", "Campinas", "13000-000")));

		assertThat(saved.getHomeAddress().getCity()).isEqualTo("Sao Paulo");
		assertThat(saved.getBillingAddress().getCity()).isEqualTo("Campinas");
	}

	@Test
	@DisplayName("query method navega na propriedade do embeddable (findByHomeAddressCity)")
	void queriesByEmbeddedProperty() {
		repository.save(new Customer("Joana",
				new Address("Rua A", "Sao Paulo", "01000-000"),
				new Address("Av. B", "Campinas", "13000-000")));
		repository.save(new Customer("Pedro",
				new Address("Rua C", "Recife", "50000-000"),
				new Address("Rua C", "Recife", "50000-000")));

		assertThat(repository.findByHomeAddressCity("Recife"))
				.singleElement()
				.extracting(Customer::getName).isEqualTo("Pedro");
	}
}
