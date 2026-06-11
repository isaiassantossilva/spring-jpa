package com.santos.spring_jpa.relationships.onetomany;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @OneToMany bidirecional (lado inverso, mappedBy). cascade = ALL salva os
 * livros junto com o autor; orphanRemoval deleta livros removidos da lista.
 * Colecoes sao LAZY por padrao.
 */
@NamedEntityGraph(name = "Author.withBooks", attributeNodes = @NamedAttributeNode("books"))
@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Book> books = new ArrayList<>();

	public Author(String name) {
		this.name = name;
	}

	/** Helpers que mantem os dois lados do relacionamento sincronizados. */
	public void addBook(Book book) {
		this.books.add(book);
		book.setAuthor(this);
	}

	public void removeBook(Book book) {
		this.books.remove(book);
		book.setAuthor(null);
	}
}
