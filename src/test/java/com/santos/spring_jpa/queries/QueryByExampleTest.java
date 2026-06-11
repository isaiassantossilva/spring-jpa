package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

/**
 * Query by Example: monta a consulta a partir de uma entidade "probe".
 * Campos primitivos (como o boolean active) entram com o valor default,
 * por isso o withIgnorePaths.
 */
@DataJpaTest
class QueryByExampleTest {

	@Autowired
	private EmployeeRepository repository;

	@BeforeEach
	void seed() {
		repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));
	}

	@Test
	@DisplayName("probe com igualdade simples")
	void simpleProbe() {
		Employee probe = new Employee();
		probe.setDepartment("IT");

		Example<Employee> example = Example.of(probe, ExampleMatcher.matching().withIgnorePaths("active"));

		assertThat(repository.findAll(example))
				.extracting(Employee::getName).containsExactlyInAnyOrder("Alice", "Bob");
		assertThat(repository.count(example)).isEqualTo(2);
	}

	@Test
	@DisplayName("matcher customizado: contains + ignoreCase")
	void customMatcher() {
		Employee probe = new Employee();
		probe.setName("ALI");

		ExampleMatcher matcher = ExampleMatcher.matching()
				.withIgnorePaths("active")
				.withMatcher("name", contains().ignoreCase());

		assertThat(repository.findOne(Example.of(probe, matcher)))
				.isPresent()
				.get().extracting(Employee::getName).isEqualTo("Alice");
	}
}
