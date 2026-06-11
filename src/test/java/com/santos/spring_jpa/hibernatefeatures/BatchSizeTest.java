package com.santos.spring_jpa.hibernatefeatures;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @BatchSize medido com as estatisticas do Hibernate: acessar a primeira
 * colecao lazy carrega em lote as colecoes de todos os Teams da sessao
 * (1 SELECT com IN, em vez de 1 por time — o classico N+1).
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BatchSizeTest {

	@Autowired
	private TeamRepository repository;

	@Autowired
	private EntityManager em;

	@Autowired
	private TestEntityManager tem;

	@Test
	@DisplayName("colecoes de varios donos sao carregadas numa unica query")
	void collectionsAreBatchLoaded() {
		for (int i = 1; i <= 3; i++) {
			Team team = new Team("Time " + i);
			team.addPlayer(new Player("Jogador " + i + "A"));
			team.addPlayer(new Player("Jogador " + i + "B"));
			this.repository.save(team);
		}
		this.tem.flush();
		this.tem.clear();

		List<Team> teams = this.repository.findAll();

		Statistics stats = this.em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
		stats.clear();

		// acessar as 3 colecoes dispara UMA query em lote, nao 3
		teams.forEach(team -> assertThat(team.getPlayers()).hasSize(2));

		assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
	}
}
