package com.santos.spring_jpa.postgres;

import com.santos.spring_jpa.queries.Employee;
import com.santos.spring_jpa.queries.EmployeeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fluxo de producao: PostgreSQL real em container (Testcontainers) com o
 * schema gerenciado pelo Flyway — o Hibernate nao cria nada (ddl-auto none,
 * que o Boot infere ao detectar o Flyway).
 *
 * - @AutoConfigureTestDatabase(replace = NONE): impede o @DataJpaTest de
 *   trocar o datasource pelo H2 em memoria;
 * - @ServiceConnection: o Boot le host/porta/credenciais do container e
 *   configura o datasource sozinho (sem @DynamicPropertySource manual);
 * - disabledWithoutDocker: o teste e pulado em maquinas sem Docker.
 */
@DataJpaTest(properties = "spring.flyway.enabled=true")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class PostgresFlywayTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

	@Autowired
	private EmployeeRepository repository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("o Flyway aplicou as migracoes e registrou no flyway_schema_history")
	void flywayAppliedMigrations() {
		List<?> history = this.em.createNativeQuery(
						"select version, description, success from flyway_schema_history order by installed_rank")
				.getResultList();

		assertThat(history).hasSize(2);
		Object[] first = (Object[]) history.getFirst();
		assertThat(first[0]).isEqualTo("1");
		assertThat(first[1]).isEqualTo("create employees");
		assertThat(first[2]).isEqualTo(true);
	}

	@Test
	@DisplayName("os dados de seed da V2 estao disponiveis")
	void seedDataIsAvailable() {
		assertThat(this.repository.count()).isEqualTo(3);
		assertThat(this.repository.findByDepartment("IT"))
				.extracting(Employee::getName)
				.containsExactlyInAnyOrder("Alice", "Bob");
	}

	@Test
	@DisplayName("o repositorio funciona igual contra o PostgreSQL real")
	void repositoryWorksAgainstRealPostgres() {
		Employee saved = this.repository.save(
				new Employee("Dora", "dora@corp.com", "Sales", new BigDecimal("6500.00"), LocalDate.of(2024, 5, 2)));

		assertThat(saved.getId()).isNotNull();
		assertThat(this.repository.findByNameContainingIgnoreCase("DORA")).hasSize(1);
		// query nativa: '||' tambem e concatenacao no PostgreSQL
		assertThat(this.repository.findByEmailDomain("@corp.com")).hasSize(4);
	}

	@Test
	@DisplayName("constraint do banco real: email duplicado viola o unique da migracao")
	void uniqueConstraintIsEnforcedByRealDatabase() {
		assertThatThrownBy(() -> this.repository.saveAndFlush(
				new Employee("Clone", "alice@corp.com", "IT", BigDecimal.TEN, LocalDate.now())))
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}
