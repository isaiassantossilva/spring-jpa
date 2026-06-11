package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.santos.spring_jpa.queries.EmployeeSpecs.hasDepartment;
import static com.santos.spring_jpa.queries.EmployeeSpecs.isActive;
import static com.santos.spring_jpa.queries.EmployeeSpecs.nameLike;
import static com.santos.spring_jpa.queries.EmployeeSpecs.salaryAtLeast;
import static org.assertj.core.api.Assertions.assertThat;

/** Specifications (Criteria API) para filtros dinamicos e combinaveis. */
@DataJpaTest
class SpecificationsTest {

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
	@DisplayName("specification simples")
	void singleSpecification() {
		assertThat(repository.findAll(hasDepartment("IT"))).hasSize(2);
	}

	@Test
	@DisplayName("combinacao com and / or")
	void combinedSpecifications() {
		Specification<Employee> itWellPaid = hasDepartment("IT").and(salaryAtLeast(new BigDecimal("6000")));
		assertThat(repository.findAll(itWellPaid))
				.extracting(Employee::getName).containsExactly("Alice");

		Specification<Employee> itOrHrActive = hasDepartment("IT").or(hasDepartment("HR")).and(isActive());
		assertThat(repository.findAll(itOrHrActive)).hasSize(3);
	}

	@Test
	@DisplayName("specification com Sort e count")
	void specificationWithSortAndCount() {
		assertThat(repository.findAll(isActive(), Sort.by("salary").descending()))
				.extracting(Employee::getName).containsExactly("Alice", "Carol", "Bob");

		assertThat(repository.count(nameLike("o"))).isEqualTo(2); // Bob, Carol
	}
}
