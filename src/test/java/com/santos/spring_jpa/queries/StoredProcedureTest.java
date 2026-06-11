package com.santos.spring_jpa.queries;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Funcao do banco registrada com CREATE ALIAS (o "stored procedure" do H2)
 * e chamada via CALL. Com Postgres/MySQL o mesmo exemplo usaria
 * @Procedure(procedureName = ...) no repositorio.
 */
@DataJpaTest
class StoredProcedureTest {

	@Autowired
	private EmployeeRepository repository;

	@Autowired
	private EntityManager em;

	@BeforeEach
	void registerAlias() {
		em.createNativeQuery("create alias if not exists plus_tax for "
						+ "'com.santos.spring_jpa.procedures.H2Functions.plusTax'")
				.executeUpdate();
	}

	@Test
	@DisplayName("CALL invoca a funcao registrada no banco")
	void callsProcedure() {
		Double result = repository.plusTax(100.0);

		assertThat(result).isCloseTo(120.0, within(0.001));
	}
}
