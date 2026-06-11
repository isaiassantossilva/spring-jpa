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

/** Projecoes: interface (closed/open), DTO record e projecao dinamica. */
@DataJpaTest
class ProjectionsTest {

	@Autowired
	private EmployeeRepository repository;

	@BeforeEach
	void seed() {
		this.repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		this.repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
	}

	@Test
	@DisplayName("projecao por interface: closed (getName) e open com SpEL (getLabel)")
	void interfaceProjection() {
		List<EmployeeNameOnly> result = this.repository.findByDepartmentOrderByName("IT");

		assertThat(result).extracting(EmployeeNameOnly::getName).containsExactly("Alice", "Bob");
		assertThat(result.getFirst().getLabel()).isEqualTo("Alice (IT)");
	}

	@Test
	@DisplayName("projecao DTO com record via constructor expression")
	void recordProjection() {
		List<EmployeeSummary> result = this.repository.findSummariesByDepartment("IT");

		assertThat(result)
				.containsExactlyInAnyOrder(
						new EmployeeSummary("Alice", new BigDecimal("9000.00")),
						new EmployeeSummary("Bob", new BigDecimal("5000.00")));
	}

	@Test
	@DisplayName("projecao dinamica: o chamador escolhe o tipo de retorno")
	void dynamicProjection() {
		List<EmployeeNameOnly> names = this.repository.findByActiveTrue(EmployeeNameOnly.class);
		List<Employee> entities = this.repository.findByActiveTrue(Employee.class);

		assertThat(names).hasSize(2).extracting(EmployeeNameOnly::getName).contains("Alice", "Bob");
		assertThat(entities).hasSize(2).allSatisfy(e -> assertThat(e.getId()).isNotNull());
	}
}
