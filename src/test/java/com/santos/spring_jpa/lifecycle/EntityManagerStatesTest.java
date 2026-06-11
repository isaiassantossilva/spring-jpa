package com.santos.spring_jpa.lifecycle;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Estados da entidade no EntityManager: transient (new), managed,
 * detached e removed — e as transicoes persist/detach/merge/remove.
 */
@DataJpaTest
class EntityManagerStatesTest {

	@Autowired
	private TestEntityManager tem;

	@Test
	@DisplayName("persist torna a entidade managed; mudancas vao via dirty checking")
	void persistAndDirtyChecking() {
		EntityManager em = tem.getEntityManager();
		Document doc = new Document("Rascunho");

		assertThat(em.contains(doc)).isFalse(); // transient

		em.persist(doc);
		assertThat(em.contains(doc)).isTrue(); // managed
		assertThat(doc.getId()).isNotNull();

		doc.setTitle("Rascunho v2"); // sem save: dirty checking
		tem.flush();
		tem.clear();

		assertThat(tem.find(Document.class, doc.getId()).getTitle()).isEqualTo("Rascunho v2");
	}

	@Test
	@DisplayName("detach desconecta a entidade; mudancas nela sao ignoradas")
	void detachIgnoresChanges() {
		EntityManager em = tem.getEntityManager();
		Document doc = tem.persistFlushFind(new Document("Original"));

		em.detach(doc);
		assertThat(em.contains(doc)).isFalse();

		doc.setTitle("Mudanca perdida");
		tem.flush();
		tem.clear();

		assertThat(tem.find(Document.class, doc.getId()).getTitle()).isEqualTo("Original");
	}

	@Test
	@DisplayName("merge copia o estado da detached para uma instancia managed")
	void mergeReattaches() {
		EntityManager em = tem.getEntityManager();
		Document doc = tem.persistFlushFind(new Document("Antes"));
		em.detach(doc);

		doc.setTitle("Depois");
		Document merged = em.merge(doc);

		assertThat(merged).isNotSameAs(doc); // merge retorna outra instancia
		assertThat(em.contains(merged)).isTrue();
		tem.flush();
		tem.clear();

		assertThat(tem.find(Document.class, doc.getId()).getTitle()).isEqualTo("Depois");
	}

	@Test
	@DisplayName("remove marca a entidade para delecao no flush")
	void removeDeletes() {
		EntityManager em = tem.getEntityManager();
		Document doc = tem.persistFlushFind(new Document("Descartavel"));

		em.remove(doc);
		assertThat(em.contains(doc)).isFalse();
		tem.flush();

		assertThat(tem.find(Document.class, doc.getId())).isNull();
	}

	@Test
	@DisplayName("getReference retorna proxy lazy sem consultar o banco")
	void getReferenceReturnsProxy() {
		EntityManager em = tem.getEntityManager();
		Long id = tem.persistAndFlush(new Document("Proxy")).getId();
		tem.clear();

		Document reference = em.getReference(Document.class, id);

		assertThat(reference.getClass()).isNotEqualTo(Document.class); // subclasse proxy
		assertThat(reference.getTitle()).isEqualTo("Proxy"); // acesso dispara o SELECT
	}
}
