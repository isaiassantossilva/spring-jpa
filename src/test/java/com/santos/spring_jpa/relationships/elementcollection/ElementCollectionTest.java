package com.santos.spring_jpa.relationships.elementcollection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/** @ElementCollection com Set de basicos e Map. */
@DataJpaTest
class ElementCollectionTest {

	@Autowired
	private ArticleRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("Set e Map de tipos basicos vao para tabelas auxiliares")
	void persistsCollections() {
		Article article = new Article("Entendendo JPA");
		article.setTags(Set.of("java", "jpa", "hibernate"));
		article.getMetadata().put("author", "zah");
		article.getMetadata().put("lang", "pt-BR");

		Long id = this.repository.saveAndFlush(article).getId();
		this.em.clear();

		Article reloaded = this.repository.findById(id).orElseThrow();
		assertThat(reloaded.getTags()).containsExactlyInAnyOrder("java", "jpa", "hibernate");
		assertThat(reloaded.getMetadata()).containsOnly(entry("author", "zah"), entry("lang", "pt-BR"));
	}

	@Test
	@DisplayName("query method pesquisa dentro da colecao (findByTagsContaining)")
	void queriesInsideCollection() {
		Article a = new Article("Sobre Java");
		a.setTags(Set.of("java"));
		this.repository.save(a);

		Article b = new Article("Sobre Python");
		b.setTags(Set.of("python"));
		this.repository.save(b);

		assertThat(this.repository.findByTagsContaining("java"))
				.singleElement()
				.extracting(Article::getTitle).isEqualTo("Sobre Java");
	}

	@Test
	@DisplayName("alterar a colecao atualiza a tabela auxiliar")
	void updatesCollection() {
		Article article = new Article("Mutavel");
		article.getTags().add("v1");
		Long id = this.repository.saveAndFlush(article).getId();

		Article managed = this.repository.findById(id).orElseThrow();
		managed.getTags().remove("v1");
		managed.getTags().add("v2");
		this.em.flush();
		this.em.clear();

		assertThat(this.repository.findById(id).orElseThrow().getTags()).containsExactly("v2");
	}
}
