package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** @Query com JPQL, SQL nativo e @Modifying (bulk update/delete). */
@DataJpaTest
class JpqlAndNativeQueryTest {

	@Autowired
	private EmployeeRepository repository;

	@Autowired
	private TestEntityManager em;

	@BeforeEach
	void seed() {
		repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		Employee dave = new Employee("Dave", "dave@oldcorp.com", "HR", new BigDecimal("4000.00"), LocalDate.of(2022, 11, 5));
		dave.setActive(false);
		repository.save(dave);
		em.flush();
	}

	@Test
	@DisplayName("JPQL com parametro nomeado (:min)")
	void jpqlNamedParameter() {
		assertThat(repository.findWellPaidActive(new BigDecimal("6000")))
				.extracting(Employee::getName).containsExactly("Alice");
	}

	@Test
	@DisplayName("JPQL com parametro posicional (?1)")
	void jpqlPositionalParameter() {
		assertThat(repository.findByDepartmentSorted("IT"))
				.extracting(Employee::getName).containsExactly("Alice", "Bob");
	}

	@Test
	@DisplayName("funcao de agregacao (avg)")
	void aggregateFunction() {
		assertThat(repository.averageSalaryByDepartment("IT"))
				.isPresent()
				.get().satisfies(avg -> assertThat(avg).isCloseTo(7000.0, within(0.01)));
	}

	@Test
	@DisplayName("constructor expression projeta direto para DTO")
	void constructorExpression() {
		assertThat(repository.findSummariesByDepartment("IT"))
				.extracting(EmployeeSummary::name)
				.containsExactlyInAnyOrder("Alice", "Bob");
	}

	@Test
	@DisplayName("query nativa com SQL puro")
	void nativeQuery() {
		assertThat(repository.findByEmailDomain("@oldcorp.com"))
				.extracting(Employee::getName).containsExactly("Dave");
	}

	@Test
	@DisplayName("@Modifying executa bulk update direto no banco")
	void modifyingBulkUpdate() {
		int affected = repository.raiseSalaryByDepartment("IT", new BigDecimal("1.10"));

		assertThat(affected).isEqualTo(2);
		// clearAutomatically = true descartou o contexto: a leitura vem do banco
		assertThat(repository.findByEmail("bob@corp.com").orElseThrow().getSalary())
				.isEqualByComparingTo("5500.00");
	}

	@Test
	@DisplayName("@Modifying executa bulk delete")
	void modifyingBulkDelete() {
		int removed = repository.purgeInactive();
		em.clear();

		assertThat(removed).isEqualTo(1);
		assertThat(repository.count()).isEqualTo(2);
	}
}
