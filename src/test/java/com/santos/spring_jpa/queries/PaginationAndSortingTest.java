package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Sort, Pageable/Page e Slice. */
@DataJpaTest
class PaginationAndSortingTest {

	@Autowired
	private EmployeeRepository repository;

	@BeforeEach
	void seed() {
		repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));
		repository.save(new Employee("Erin", "erin@corp.com", "Sales", new BigDecimal("6000.00"), LocalDate.of(2023, 2, 20)));
	}

	@Test
	@DisplayName("Sort dinamico no query method")
	void dynamicSort() {
		assertThat(repository.findByActiveTrue(Sort.by("salary").descending()))
				.extracting(Employee::getName)
				.containsExactly("Alice", "Carol", "Erin", "Bob");

		assertThat(repository.findByActiveTrue(Sort.by(Sort.Order.asc("department"), Sort.Order.desc("name"))))
				.extracting(Employee::getName)
				.containsExactly("Carol", "Bob", "Alice", "Erin");
	}

	@Test
	@DisplayName("Page traz conteudo + total de elementos e paginas")
	void pagination() {
		Page<Employee> firstPage = repository.findByActiveTrue(
				PageRequest.of(0, 2, Sort.by("salary").descending()));

		assertThat(firstPage.getTotalElements()).isEqualTo(4);
		assertThat(firstPage.getTotalPages()).isEqualTo(2);
		assertThat(firstPage.getContent()).extracting(Employee::getName).containsExactly("Alice", "Carol");
		assertThat(firstPage.hasNext()).isTrue();

		Page<Employee> secondPage = repository.findByActiveTrue(firstPage.nextPageable());
		assertThat(secondPage.getContent()).extracting(Employee::getName).containsExactly("Erin", "Bob");
		assertThat(secondPage.hasNext()).isFalse();
	}

	@Test
	@DisplayName("Slice sabe se ha proxima pagina sem executar count")
	void slice() {
		Slice<Employee> slice = repository.findBySalaryGreaterThan(
				new BigDecimal("4500"), PageRequest.of(0, 3, Sort.by("name")));

		assertThat(slice.getContent()).hasSize(3);
		assertThat(slice.hasNext()).isTrue();
		assertThat(slice.nextPageable().getPageNumber()).isEqualTo(1);
	}
}
