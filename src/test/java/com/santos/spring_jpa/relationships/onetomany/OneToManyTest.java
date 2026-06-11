package com.santos.spring_jpa.relationships.onetomany;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** @OneToMany/@ManyToOne bidirecional: cascade, orphanRemoval, lazy e N+1. */
@DataJpaTest
class OneToManyTest {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private TestEntityManager em;

	private Author persistAuthorWithTwoBooks(String name) {
		Author author = new Author(name);
		author.addBook(new Book("Livro 1 de " + name));
		author.addBook(new Book("Livro 2 de " + name));
		Author saved = authorRepository.saveAndFlush(author);
		em.clear();
		return saved;
	}

	@Test
	@DisplayName("cascade = ALL persiste os livros junto com o autor")
	void cascadePersist() {
		Author saved = persistAuthorWithTwoBooks("Machado");

		assertThat(bookRepository.findByAuthorName("Machado")).hasSize(2);
		assertThat(bookRepository.findAll())
				.allSatisfy(book -> assertThat(book.getAuthor().getId()).isEqualTo(saved.getId()));
	}

	@Test
	@DisplayName("colecao @OneToMany e LAZY: so carrega ao ser acessada")
	void lazyLoading() {
		Long id = persistAuthorWithTwoBooks("Clarice").getId();

		Author reloaded = authorRepository.findById(id).orElseThrow();
		assertThat(Hibernate.isInitialized(reloaded.getBooks())).isFalse();

		// acessar a colecao dentro da transacao dispara o SELECT
		assertThat(reloaded.getBooks()).hasSize(2);
		assertThat(Hibernate.isInitialized(reloaded.getBooks())).isTrue();
	}

	@Test
	@DisplayName("JOIN FETCH e @EntityGraph carregam a colecao na mesma query")
	void fetchStrategies() {
		persistAuthorWithTwoBooks("Guimaraes");

		Author fetched = authorRepository.findWithBooksByName("Guimaraes").orElseThrow();
		assertThat(Hibernate.isInitialized(fetched.getBooks())).isTrue();
		em.clear();

		assertThat(authorRepository.findWithBooksByNameContaining("Guima"))
				.singleElement()
				.satisfies(a -> assertThat(Hibernate.isInitialized(a.getBooks())).isTrue());
		em.clear();

		// @NamedEntityGraph definido na entidade, referenciado pelo nome
		Author viaGraph = authorRepository.findOneWithGraphByName("Guimaraes").orElseThrow();
		assertThat(Hibernate.isInitialized(viaGraph.getBooks())).isTrue();
	}

	@Test
	@DisplayName("orphanRemoval deleta o livro removido da colecao")
	void orphanRemoval() {
		Long id = persistAuthorWithTwoBooks("Jorge").getId();

		Author author = authorRepository.findById(id).orElseThrow();
		author.removeBook(author.getBooks().getFirst());
		em.flush();

		assertThat(bookRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("deletar o autor remove os livros em cascata")
	void cascadeRemove() {
		Long id = persistAuthorWithTwoBooks("Cecilia").getId();

		authorRepository.deleteById(id);
		em.flush();

		assertThat(bookRepository.count()).isZero();
	}
}
