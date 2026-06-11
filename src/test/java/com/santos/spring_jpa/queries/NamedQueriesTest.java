package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @NamedQuery e @NamedNativeQuery declaradas na entidade e resolvidas pelo
 * Spring Data pela convencao "Employee.<nomeDoMetodo>".
 */
@DataJpaTest
class NamedQueriesTest {

	@Autowired
	private EmployeeRepository repository;

	@BeforeEach
	void seed() {
		this.repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		this.repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		this.repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));
	}

	@Test
	@DisplayName("@NamedQuery: o metodo do repositorio casa com o nome declarado na entidade")
	void namedJpqlQuery() {
		assertThat(this.repository.findEarningMoreThan(new BigDecimal("6000")))
				.extracting(Employee::getName)
				.containsExactly("Alice", "Carol");
	}

	@Test
	@DisplayName("@NamedNativeQuery + @SqlResultSetMapping projeta SQL nativo no record")
	void namedNativeQueryWithMapping() {
		assertThat(this.repository.findSummariesNative("IT"))
				.containsExactlyInAnyOrder(
						new EmployeeSummary("Alice", new BigDecimal("9000.00")),
						new EmployeeSummary("Bob", new BigDecimal("5000.00")));
	}
}
