package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Query methods derivados do nome do metodo. */
@DataJpaTest
class DerivedQueryMethodsTest {

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
		repository.save(new Employee("Erin", "erin@corp.com", "Sales", new BigDecimal("6000.00"), LocalDate.of(2023, 2, 20)));
	}

	@Test
	@DisplayName("igualdade, In, And/Or e booleanos (True/False)")
	void equalityAndLogicalOperators() {
		assertThat(repository.findByDepartment("IT")).hasSize(2);
		assertThat(repository.findByDepartmentIn(List.of("IT", "Sales"))).hasSize(3);
		assertThat(repository.findByActiveTrue()).hasSize(4);
		assertThat(repository.findByActiveFalse()).extracting(Employee::getName).containsExactly("Dave");
		assertThat(repository.findByDepartmentAndActiveTrue("HR")).extracting(Employee::getName).containsExactly("Carol");
		assertThat(repository.findByDepartmentOrDepartment("IT", "Sales")).hasSize(3);
	}

	@Test
	@DisplayName("Containing, StartingWith e IgnoreCase em strings")
	void stringOperators() {
		assertThat(repository.findByNameContainingIgnoreCase("ALI")).extracting(Employee::getName).containsExactly("Alice");
		assertThat(repository.findByNameStartingWith("Ca")).extracting(Employee::getName).containsExactly("Carol");
	}

	@Test
	@DisplayName("Between, GreaterThanEqual e After em numeros e datas")
	void rangeOperators() {
		assertThat(repository.findBySalaryBetween(new BigDecimal("5000"), new BigDecimal("7000")))
				.extracting(Employee::getName).containsExactlyInAnyOrder("Bob", "Carol", "Erin");
		assertThat(repository.findBySalaryGreaterThanEqual(new BigDecimal("7000"))).hasSize(2);
		assertThat(repository.findByHireDateAfter(LocalDate.of(2021, 12, 31)))
				.extracting(Employee::getName).containsExactlyInAnyOrder("Dave", "Erin");
	}

	@Test
	@DisplayName("Top/First com OrderBy limitam o resultado")
	void limitingResults() {
		assertThat(repository.findTop3ByOrderBySalaryDesc())
				.extracting(Employee::getName).containsExactly("Alice", "Carol", "Erin");
		assertThat(repository.findFirstByDepartmentOrderByHireDateAsc("IT"))
				.isPresent().get().extracting(Employee::getName).isEqualTo("Alice");
	}

	@Test
	@DisplayName("count, exists e Optional para resultado unico")
	void countExistsAndOptional() {
		assertThat(repository.countByDepartment("HR")).isEqualTo(2);
		assertThat(repository.existsByEmail("bob@corp.com")).isTrue();
		assertThat(repository.existsByEmail("zed@corp.com")).isFalse();
		assertThat(repository.findByEmail("erin@corp.com")).isPresent();
		assertThat(repository.findByEmail("zed@corp.com")).isEmpty();
	}

	@Test
	@DisplayName("derived delete remove e retorna a quantidade")
	void derivedDelete() {
		long removed = repository.deleteByActiveFalse();

		assertThat(removed).isEqualTo(1);
		assertThat(repository.count()).isEqualTo(4);
	}
}
