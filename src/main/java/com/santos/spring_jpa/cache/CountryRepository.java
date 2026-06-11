package com.santos.spring_jpa.cache;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

	/**
	 * Query cache: alem do cache de entidades, o RESULTADO desta consulta
	 * (a lista de ids) e cacheado. Exige hibernate.cache.use_query_cache.
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Country> findByName(String name);
}
