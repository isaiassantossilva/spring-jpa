package com.santos.spring_jpa.hibernatefeatures;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

/**
 * @BatchSize: ao acessar uma colecao lazy, o Hibernate carrega de uma vez as
 * colecoes de ate N donos presentes na sessao (um IN em vez de N+1 SELECTs).
 */
@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	@BatchSize(size = 16)
	private List<Player> players = new ArrayList<>();

	public Team(String name) {
		this.name = name;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
		player.setTeam(this);
	}
}
