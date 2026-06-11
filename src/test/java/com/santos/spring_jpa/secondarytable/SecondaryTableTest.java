package com.santos.spring_jpa.secondarytable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** @SecondaryTable: uma entidade, duas tabelas ligadas pela PK. */
@DataJpaTest
class SecondaryTableTest {

	@Autowired
	private BlogPostRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("o insert grava nas duas tabelas e o load junta de volta")
	void splitsAcrossTwoTables() {
		Long id = repository.saveAndFlush(
				new BlogPost("JPA na pratica", "Conteudo longo...", "jpa-na-pratica")).getId();
		em.clear();

		BlogPost reloaded = repository.findById(id).orElseThrow();
		assertThat(reloaded.getTitle()).isEqualTo("JPA na pratica");
		assertThat(reloaded.getContent()).isEqualTo("Conteudo longo...");

		Number mainRows = (Number) em.getEntityManager()
				.createNativeQuery("select count(*) from blog_posts").getSingleResult();
		Number detailRows = (Number) em.getEntityManager()
				.createNativeQuery("select count(*) from blog_post_details").getSingleResult();
		assertThat(mainRows.intValue()).isEqualTo(1);
		assertThat(detailRows.intValue()).isEqualTo(1);
	}

	@Test
	@DisplayName("query method filtra por coluna da tabela secundaria")
	void queriesSecondaryTableColumn() {
		repository.save(new BlogPost("Post A", "...", "seo-aaa"));
		repository.save(new BlogPost("Post B", "...", "seo-bbb"));

		assertThat(repository.findBySeoTitleContaining("bbb"))
				.singleElement()
				.extracting(BlogPost::getTitle).isEqualTo("Post B");
	}
}
