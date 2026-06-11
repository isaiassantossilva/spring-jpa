package com.santos.spring_jpa.ids;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Estrategias de geracao de ID e chaves compostas. */
@DataJpaTest
class IdGenerationTest {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private CountryLanguageRepository countryLanguageRepository;

	@Test
	@DisplayName("SEQUENCE: ids vem da sequence customizada (initialValue = 1000)")
	void sequenceGeneration() {
		Invoice first = invoiceRepository.save(new Invoice(new BigDecimal("100.00")));
		Invoice second = invoiceRepository.save(new Invoice(new BigDecimal("200.00")));

		assertThat(first.getId()).isGreaterThanOrEqualTo(1000L);
		assertThat(second.getId()).isGreaterThan(first.getId());
	}

	@Test
	@DisplayName("UUID: id gerado automaticamente como UUID")
	void uuidGeneration() {
		Ticket ticket = ticketRepository.save(new Ticket("Sistema fora do ar"));

		assertThat(ticket.getId()).isNotNull().isInstanceOf(UUID.class);
		assertThat(ticketRepository.findById(ticket.getId())).isPresent();
	}

	@Test
	@DisplayName("@EmbeddedId: busca usando a chave composta")
	void embeddedIdCompositeKey() {
		EnrollmentId id = new EnrollmentId(1L, 42L);
		enrollmentRepository.save(new Enrollment(id, 9.5));

		Enrollment found = enrollmentRepository.findById(new EnrollmentId(1L, 42L)).orElseThrow();
		assertThat(found.getGrade()).isEqualTo(9.5);
	}

	@Test
	@DisplayName("@IdClass: dois campos @Id formam a chave")
	void idClassCompositeKey() {
		countryLanguageRepository.save(new CountryLanguage("BR", "pt", true));
		countryLanguageRepository.save(new CountryLanguage("BR", "es", false));

		assertThat(countryLanguageRepository.findByCountry("BR")).hasSize(2);
		assertThat(countryLanguageRepository.findById(new CountryLanguageId("BR", "pt")))
				.isPresent()
				.get().extracting(CountryLanguage::isOfficial).isEqualTo(true);
	}
}
