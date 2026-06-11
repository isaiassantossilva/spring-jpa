package com.santos.spring_jpa.relationships.onetomany;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

	/** Navegacao na associacao: where book.author.name = ? */
	List<Book> findByAuthorName(String authorName);
}
