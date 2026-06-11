package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Fragment customizado (interface + Impl) usando a Criteria API crua. */
@DataJpaTest
class CustomRepositoryTest {

	@Autowired
	private EmployeeRepository repository;

	@BeforeEach
	void seed() {
		repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));
		Employee dave = new Employee("Dave", "dave@corp.com", "HR", new BigDecimal("4000.00"), LocalDate.of(2022, 11, 5));
		dave.setActive(false);
		repository.save(dave);
	}

	@Test
	@DisplayName("sem filtros: retorna todos, ordenados por nome")
	void noFilters() {
		assertThat(repository.search(null, null, null))
				.extracting(Employee::getName)
				.containsExactly("Alice", "Bob", "Carol", "Dave");
	}

	@Test
	@DisplayName("filtros individuais sao aplicados so quando informados")
	void individualFilters() {
		assertThat(repository.search("IT", null, null)).hasSize(2);
		assertThat(repository.search(null, new BigDecimal("6000"), null))
				.extracting(Employee::getName).containsExactly("Alice", "Carol");
		assertThat(repository.search(null, null, false))
				.extracting(Employee::getName).containsExactly("Dave");
	}

	@Test
	@DisplayName("filtros combinados")
	void combinedFilters() {
		assertThat(repository.search("HR", new BigDecimal("5000"), true))
				.extracting(Employee::getName).containsExactly("Carol");
	}

	@Test
	@DisplayName("o fragment convive com os query methods no mesmo repositorio")
	void fragmentCoexistsWithDerivedQueries() {
		assertThat(repository.findByDepartment("IT")).hasSize(2);
		assertThat(repository.search("IT", new BigDecimal("8000"), null))
				.extracting(Employee::getName).containsExactly("Alice");
	}
}
