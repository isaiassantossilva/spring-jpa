package com.santos.spring_jpa.hibernatefeatures;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** @Formula, @NaturalId e @Filter — extensoes do Hibernate ao JPA. */
@DataJpaTest
class HibernateFeaturesTest {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private SoftNoteRepository softNoteRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private TestEntityManager tem;

	@Test
	@DisplayName("@Formula calcula o atributo no SELECT (read-only)")
	void formulaIsComputedOnLoad() {
		Long id = this.movieRepository.saveAndFlush(
				new Movie("Matrix", "tt0133093", new BigDecimal("50.00"))).getId();
		this.tem.clear();

		Movie reloaded = this.movieRepository.findById(id).orElseThrow();
		assertThat(reloaded.getPriceWithTax()).isEqualByComparingTo("60.00");
	}

	@Test
	@DisplayName("@NaturalId: lookup pela chave de negocio via Session")
	void naturalIdLookup() {
		this.movieRepository.saveAndFlush(new Movie("Matrix", "tt0133093", new BigDecimal("50.00")));
		this.tem.clear();

		Movie movie = this.em.unwrap(Session.class)
				.bySimpleNaturalId(Movie.class)
				.load("tt0133093");

		assertThat(movie.getTitle()).isEqualTo("Matrix");
	}

	@Test
	@DisplayName("@Filter: restricao parametrizada ativada por sessao")
	void filterRestrictsQueriesWhenEnabled() {
		this.movieRepository.save(new Movie("Barato", "tt0000001", new BigDecimal("10.00")));
		this.movieRepository.save(new Movie("Caro", "tt0000002", new BigDecimal("80.00")));
		this.tem.flush();

		assertThat(this.movieRepository.findAll()).hasSize(2);

		this.em.unwrap(Session.class)
				.enableFilter("minPrice")
				.setParameter("min", new BigDecimal("50.00"));

		assertThat(this.movieRepository.findAll())
				.singleElement()
				.extracting(Movie::getTitle).isEqualTo("Caro");
	}

	@Test
	@DisplayName("@SQLRestriction esconde linhas que nao atendem ao WHERE fixo")
	void sqlRestrictionHidesRows() {
		this.couponRepository.save(new Coupon("ATIVO10"));
		this.tem.flush();

		// insere um cupom expirado por baixo dos panos (SQL nativo)
		this.em.createNativeQuery("insert into coupons (code, expired) values ('VELHO99', true)")
				.executeUpdate();

		Number totalRows = (Number) this.em.createNativeQuery("select count(*) from coupons").getSingleResult();
		assertThat(totalRows.intValue()).isEqualTo(2);
		assertThat(this.couponRepository.findAll())
				.singleElement()
				.extracting(Coupon::getCode).isEqualTo("ATIVO10");
	}

	@Test
	@DisplayName("@SoftDelete: delete vira UPDATE e a linha some das leituras JPA")
	void softDeleteKeepsRowInDatabase() {
		SoftNote note = this.softNoteRepository.saveAndFlush(new SoftNote("nao me apague de verdade"));

		this.softNoteRepository.delete(note);
		this.tem.flush();
		this.tem.clear();

		assertThat(this.softNoteRepository.findAll()).isEmpty();

		Number rawRows = (Number) this.em.createNativeQuery("select count(*) from soft_notes").getSingleResult();
		Number deletedFlag = (Number) this.em.createNativeQuery(
				"select count(*) from soft_notes where deleted = true").getSingleResult();
		assertThat(rawRows.intValue()).isEqualTo(1);
		assertThat(deletedFlag.intValue()).isEqualTo(1);
	}
}
