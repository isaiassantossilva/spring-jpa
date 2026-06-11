package com.santos.spring_jpa.secondarytable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @SecondaryTable: uma entidade espalhada em duas tabelas ligadas pela PK.
 * Util para isolar colunas grandes/raramente lidas sem criar outra entidade.
 */
@Entity
@Table(name = "blog_posts")
@SecondaryTable(name = "blog_post_details", pkJoinColumns = @PrimaryKeyJoinColumn(name = "post_id"))
@Getter
@Setter
@NoArgsConstructor
public class BlogPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(table = "blog_post_details")
	private String content;

	@Column(table = "blog_post_details", name = "seo_title")
	private String seoTitle;

	public BlogPost(String title, String content, String seoTitle) {
		this.title = title;
		this.content = content;
		this.seoTitle = seoTitle;
	}
}
