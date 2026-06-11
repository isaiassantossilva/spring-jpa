package com.santos.spring_jpa.relationships.onetomany;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

	/** JOIN FETCH evita o problema N+1 carregando a colecao na mesma query. */
	@Query("select distinct a from Author a left join fetch a.books where a.name = :name")
	Optional<Author> findWithBooksByName(@Param("name") String name);

	/** Alternativa declarativa ao JOIN FETCH. */
	@EntityGraph(attributePaths = "books")
	List<Author> findWithBooksByNameContaining(String fragment);

	/** Grafo nomeado, definido com @NamedEntityGraph na entidade. */
	@EntityGraph("Author.withBooks")
	Optional<Author> findOneWithGraphByName(String name);
}
