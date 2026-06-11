package com.santos.spring_jpa.collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** @OrderColumn (posicao persistida) vs @OrderBy (ORDER BY no load). */
@DataJpaTest
class CollectionOrderingTest {

	@Autowired
	private PlaylistRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("@OrderColumn preserva a ordem de insercao da lista")
	void orderColumnKeepsListOrder() {
		Playlist playlist = new Playlist("Rock");
		playlist.getTracks().addAll(List.of("Intro", "Refrao", "Solo", "Final"));
		Long id = this.repository.saveAndFlush(playlist).getId();
		this.em.clear();

		Playlist reloaded = this.repository.findById(id).orElseThrow();
		assertThat(reloaded.getTracks()).containsExactly("Intro", "Refrao", "Solo", "Final");
	}

	@Test
	@DisplayName("@OrderColumn reflete reordenacoes feitas na lista")
	void orderColumnTracksReordering() {
		Playlist playlist = new Playlist("Mutavel");
		playlist.getTracks().addAll(List.of("A", "B", "C"));
		Long id = this.repository.saveAndFlush(playlist).getId();

		Playlist managed = this.repository.findById(id).orElseThrow();
		managed.getTracks().remove("B");
		managed.getTracks().addFirst("Z");
		this.em.flush();
		this.em.clear();

		assertThat(this.repository.findById(id).orElseThrow().getTracks())
				.containsExactly("Z", "A", "C");
	}

	@Test
	@DisplayName("@OrderBy ordena pelos dados ao carregar, ignorando a ordem de insercao")
	void orderByOrdersOnLoad() {
		Playlist playlist = new Playlist("Hits");
		playlist.addSong(new Song("Mediana", 3));
		playlist.addSong(new Song("Ruim", 1));
		playlist.addSong(new Song("Otima", 5));
		playlist.addSong(new Song("Boa", 5));
		Long id = this.repository.saveAndFlush(playlist).getId();
		this.em.clear();

		Playlist reloaded = this.repository.findById(id).orElseThrow();
		assertThat(reloaded.getSongs())
				.extracting(Song::getTitle)
				.containsExactly("Boa", "Otima", "Mediana", "Ruim");
	}
}
