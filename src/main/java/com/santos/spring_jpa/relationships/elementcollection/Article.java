package com.santos.spring_jpa.relationships.elementcollection;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ElementCollection: colecoes de tipos basicos ou embeddables, sem criar
 * uma entidade separada. Cada colecao vira uma tabela auxiliar.
 */
@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@ElementCollection
	@CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
	@Column(name = "tag")
	private Set<String> tags = new HashSet<>();

	@ElementCollection
	@CollectionTable(name = "article_metadata", joinColumns = @JoinColumn(name = "article_id"))
	@MapKeyColumn(name = "meta_key")
	@Column(name = "meta_value")
	private Map<String, String> metadata = new HashMap<>();

	public Article(String title) {
		this.title = title;
	}
}
