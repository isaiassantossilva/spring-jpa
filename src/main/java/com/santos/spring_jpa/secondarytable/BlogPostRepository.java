package com.santos.spring_jpa.secondarytable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

	/** O JPA resolve o join entre as duas tabelas de forma transparente. */
	List<BlogPost> findBySeoTitleContaining(String fragment);
}
