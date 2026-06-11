package com.santos.spring_jpa.collections;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordenacao de colecoes:
 * - @OrderColumn persiste a POSICAO da lista numa coluna (ordem da aplicacao);
 * - @OrderBy apenas adiciona ORDER BY ao carregar (ordem derivada dos dados).
 */
@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
public class Playlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ElementCollection
	@CollectionTable(name = "playlist_tracks", joinColumns = @JoinColumn(name = "playlist_id"))
	@OrderColumn(name = "position")
	@Column(name = "track")
	private List<String> tracks = new ArrayList<>();

	@OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("rating desc, title asc")
	private List<Song> songs = new ArrayList<>();

	public Playlist(String name) {
		this.name = name;
	}

	public void addSong(Song song) {
		songs.add(song);
		song.setPlaylist(this);
	}
}
