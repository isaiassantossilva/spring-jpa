package com.santos.spring_jpa.relationships.associationentity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Many-to-many com colunas extras: em vez de @ManyToMany + @JoinTable, a
 * tabela associativa vira uma entidade propria com @EmbeddedId, e cada parte
 * da chave e ligada a associacao correspondente via @MapsId.
 */
@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
public class Assignment {

	@EmbeddedId
	private AssignmentId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("developerId")
	@JoinColumn(name = "developer_id")
	private Developer developer;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("projectId")
	@JoinColumn(name = "project_id")
	private Project project;

	// as colunas extras que o @ManyToMany puro nao permite
	private String role;

	private LocalDate assignedAt;

	public Assignment(Developer developer, Project project, String role, LocalDate assignedAt) {
		this.id = new AssignmentId(developer.getId(), project.getId());
		this.developer = developer;
		this.project = project;
		this.role = role;
		this.assignedAt = assignedAt;
	}
}
