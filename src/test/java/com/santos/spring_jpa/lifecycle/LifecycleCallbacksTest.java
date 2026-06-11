package com.santos.spring_jpa.lifecycle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** Callbacks JPA: @PrePersist, @PostPersist, @PreUpdate, @PreRemove, @PostLoad. */
@DataJpaTest
class LifecycleCallbacksTest {

	@Autowired
	private DocumentRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("@PrePersist/@PostPersist disparam no insert (slug gerado)")
	void persistCallbacks() {
		Document doc = this.repository.saveAndFlush(new Document("Meu Primeiro Post"));

		assertThat(doc.getSlug()).isEqualTo("meu-primeiro-post");
		assertThat(doc.getFiredCallbacks()).containsExactly("PrePersist", "PostPersist");
	}

	@Test
	@DisplayName("@PostLoad dispara ao carregar do banco")
	void loadCallback() {
		Long id = this.repository.saveAndFlush(new Document("Carregado")).getId();
		this.em.clear();

		Document reloaded = this.repository.findById(id).orElseThrow();
		assertThat(reloaded.getFiredCallbacks()).containsExactly("PostLoad");
	}

	@Test
	@DisplayName("@PreUpdate dispara no update (lastModified preenchido)")
	void updateCallback() {
		Document doc = this.repository.saveAndFlush(new Document("Vai Mudar"));
		assertThat(doc.getLastModified()).isNull();

		doc.setTitle("Mudou");
		this.em.flush();

		assertThat(doc.getLastModified()).isNotNull();
		assertThat(doc.getFiredCallbacks()).contains("PreUpdate");
	}

	@Test
	@DisplayName("@PreRemove dispara antes do delete")
	void removeCallback() {
		Document doc = this.repository.saveAndFlush(new Document("Efemero"));

		this.repository.delete(doc);
		this.em.flush();

		assertThat(doc.getFiredCallbacks()).contains("PreRemove");
		assertThat(this.repository.count()).isZero();
	}
}
