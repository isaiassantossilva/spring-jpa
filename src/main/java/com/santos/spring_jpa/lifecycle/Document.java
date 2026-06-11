package com.santos.spring_jpa.lifecycle;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Callbacks de ciclo de vida JPA: @PrePersist, @PostPersist, @PreUpdate,
 * @PreRemove e @PostLoad. A lista transient registra quais foram disparados.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String slug;

	private Instant lastModified;

	@Transient
	private List<String> firedCallbacks = new ArrayList<>();

	public Document(String title) {
		this.title = title;
	}

	@PrePersist
	void onPrePersist() {
		slug = title.toLowerCase().replace(' ', '-');
		firedCallbacks.add("PrePersist");
	}

	@PostPersist
	void onPostPersist() {
		firedCallbacks.add("PostPersist");
	}

	@PreUpdate
	void onPreUpdate() {
		lastModified = Instant.now();
		firedCallbacks.add("PreUpdate");
	}

	@PreRemove
	void onPreRemove() {
		firedCallbacks.add("PreRemove");
	}

	@PostLoad
	void onPostLoad() {
		firedCallbacks.add("PostLoad");
	}
}
