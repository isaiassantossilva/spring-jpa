package com.santos.spring_jpa.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** AttributeConverter: enum -> codigo e List<String> -> CSV. */
@DataJpaTest
class AttributeConverterTest {

	@Autowired
	private SubscriptionRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("ida e volta: o atributo e reconstruido pelo converter")
	void roundTrip() {
		Long id = this.repository.saveAndFlush(
				new Subscription("zah", SubscriptionTier.PRO, List.of("api", "reports"))).getId();
		this.em.clear();

		Subscription reloaded = this.repository.findById(id).orElseThrow();

		assertThat(reloaded.getTier()).isEqualTo(SubscriptionTier.PRO);
		assertThat(reloaded.getFeatures()).containsExactly("api", "reports");
	}

	@Test
	@DisplayName("no banco fica o valor convertido ('P' e CSV), nao a representacao Java")
	void databaseStoresConvertedValues() {
		Long id = this.repository.saveAndFlush(
				new Subscription("acme", SubscriptionTier.ENTERPRISE, List.of("sso", "audit"))).getId();

		Object[] row = (Object[]) this.em.getEntityManager()
				.createNativeQuery("select tier, features from subscriptions where id = :id")
				.setParameter("id", id)
				.getSingleResult();

		assertThat(row[0]).hasToString("E"); // coluna char(1) volta como Character
		assertThat(row[1]).isEqualTo("sso,audit");
	}

	@Test
	@DisplayName("parametros de query method tambem passam pelo converter")
	void queryParameterIsConverted() {
		this.repository.save(new Subscription("a", SubscriptionTier.FREE, List.of()));
		this.repository.save(new Subscription("b", SubscriptionTier.PRO, List.of("api")));

		assertThat(this.repository.findByTier(SubscriptionTier.PRO))
				.singleElement()
				.extracting(Subscription::getCustomer).isEqualTo("b");
	}

	@Test
	@DisplayName("lista vazia vira null no banco e volta como lista vazia")
	void emptyListHandling() {
		Long id = this.repository.saveAndFlush(
				new Subscription("vazio", SubscriptionTier.FREE, List.of())).getId();
		this.em.clear();

		assertThat(this.repository.findById(id).orElseThrow().getFeatures()).isEmpty();
	}
}
