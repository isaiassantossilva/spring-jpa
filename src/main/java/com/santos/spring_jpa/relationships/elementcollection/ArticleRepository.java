package com.santos.spring_jpa.relationships.elementcollection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	/** Pesquisa dentro da colecao de elementos. */
	List<Article> findByTagsContaining(String tag);
}
