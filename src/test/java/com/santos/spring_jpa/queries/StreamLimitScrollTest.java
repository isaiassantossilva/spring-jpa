package com.santos.spring_jpa.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/** Retornos alternativos: Stream, Limit dinamico e Scroll API (keyset/offset). */
@DataJpaTest
class StreamLimitScrollTest {

	@Autowired
	private EmployeeRepository repository;

	@BeforeEach
	void seed() {
		this.repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		this.repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		this.repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));
		this.repository.save(new Employee("Erin", "erin@corp.com", "Sales", new BigDecimal("6000.00"), LocalDate.of(2023, 2, 20)));
	}

	@Test
	@DisplayName("Stream processa resultados sem materializar a lista inteira")
	void streamResults() {
		// precisa de transacao ativa (o @DataJpaTest fornece) e de fechamento
		try (Stream<Employee> stream = this.repository.streamActive()) {
			List<String> names = stream
					.filter(e -> e.getSalary().compareTo(new BigDecimal("5500")) > 0)
					.map(Employee::getName)
					.toList();
			assertThat(names).containsExactlyInAnyOrder("Alice", "Carol", "Erin");
		}
	}

	@Test
	@DisplayName("Limit define o tamanho do resultado em tempo de chamada")
	void dynamicLimit() {
		assertThat(this.repository.findByActiveTrueOrderBySalaryDesc(Limit.of(2)))
				.extracting(Employee::getName).containsExactly("Alice", "Carol");

		assertThat(this.repository.findByActiveTrueOrderBySalaryDesc(Limit.unlimited())).hasSize(4);
	}

	@Test
	@DisplayName("Scroll API com keyset: continua de onde parou sem OFFSET")
	void keysetScrolling() {
		Window<Employee> first = this.repository
				.findTop2ByActiveTrueOrderBySalaryDescIdAsc(ScrollPosition.keyset());

		assertThat(first.getContent()).extracting(Employee::getName).containsExactly("Alice", "Carol");
		assertThat(first.hasNext()).isTrue();

		Window<Employee> second = this.repository
				.findTop2ByActiveTrueOrderBySalaryDescIdAsc(first.positionAt(first.getContent().getLast()));

		assertThat(second.getContent()).extracting(Employee::getName).containsExactly("Erin", "Bob");
	}
}
