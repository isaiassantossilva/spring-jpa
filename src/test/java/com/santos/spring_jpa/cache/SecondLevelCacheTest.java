package com.santos.spring_jpa.cache;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cache de segundo nivel com JCache/EhCache, habilitado so neste teste.
 *
 * O cache vive no EntityManagerFactory e e compartilhado ENTRE transacoes —
 * com READ_WRITE, uma entrada gravada na transacao corrente nem e legivel
 * por ela mesma (controle por timestamp). Por isso este teste desliga a
 * transacao unica do @DataJpaTest: cada chamada de repositorio roda na sua
 * propria transacao, como numa aplicacao real.
 *
 * missing_cache_strategy=create cria as regioes na hora (em producao,
 * defina-as no ehcache.xml com tamanho/TTL adequados).
 */
@DataJpaTest(properties = {
		"spring.jpa.properties.hibernate.cache.use_second_level_cache=true",
		"spring.jpa.properties.hibernate.cache.use_query_cache=true",
		"spring.jpa.properties.hibernate.cache.region.factory_class=jcache",
		"spring.jpa.properties.hibernate.javax.cache.missing_cache_strategy=create",
		"spring.jpa.properties.hibernate.generate_statistics=true"
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SecondLevelCacheTest {

	@Autowired
	private CountryRepository repository;

	@Autowired
	private EntityManager em;

	private Statistics stats;

	@BeforeEach
	void setUp() {
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.stats = this.em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
		this.stats.clear();
	}

	@AfterEach
	void cleanUp() {
		this.repository.deleteAll();
	}

	@Test
	@DisplayName("segunda leitura (em outra transacao) vem do cache, sem ir ao banco")
	void entityCacheHit() {
		Long id = this.repository.save(new Country("Brasil")).getId();
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.stats.clear();

		this.repository.findById(id).orElseThrow(); // 1a leitura: miss no cache + put
		this.repository.findById(id).orElseThrow(); // 2a leitura: hit no 2o nivel

		assertThat(this.stats.getSecondLevelCacheMissCount()).isEqualTo(1);
		assertThat(this.stats.getSecondLevelCachePutCount()).isEqualTo(1);
		assertThat(this.stats.getSecondLevelCacheHitCount()).isEqualTo(1);

		// API padrao do JPA para inspecionar o cache compartilhado
		assertThat(this.em.getEntityManagerFactory().getCache().contains(Country.class, id)).isTrue();
	}

	@Test
	@DisplayName("query cache: a mesma consulta repetida nao reexecuta o SQL")
	void queryCacheHit() {
		this.repository.save(new Country("Japao"));
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.stats.clear();

		this.repository.findByName("Japao"); // executa o SQL e cacheia o resultado
		this.repository.findByName("Japao"); // resolvido pelo query cache

		assertThat(this.stats.getQueryCachePutCount()).isEqualTo(1);
		assertThat(this.stats.getQueryCacheHitCount()).isEqualTo(1);
		assertThat(this.stats.getQueryExecutionCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("alterar a entidade invalida/atualiza a entrada no cache")
	void updateKeepsCacheConsistent() {
		Long id = this.repository.save(new Country("Tchecoslovaquia")).getId();
		this.repository.findById(id).orElseThrow(); // entra no cache

		Country copy = this.repository.findById(id).orElseThrow();
		copy.setName("Tchequia");
		this.repository.save(copy);

		Country reloaded = this.repository.findById(id).orElseThrow();
		assertThat(reloaded.getName()).isEqualTo("Tchequia"); // nada de valor velho
	}
}
