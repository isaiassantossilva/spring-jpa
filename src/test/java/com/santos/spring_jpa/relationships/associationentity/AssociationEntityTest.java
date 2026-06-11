package com.santos.spring_jpa.relationships.associationentity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Many-to-many com colunas extras via entidade associativa (@EmbeddedId + @MapsId). */
@DataJpaTest
class AssociationEntityTest {

	@Autowired
	private DeveloperRepository developerRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private AssignmentRepository assignmentRepository;

	@Autowired
	private TestEntityManager em;

	private Developer ana;
	private Project portal;

	@BeforeEach
	void setUp() {
		this.ana = this.developerRepository.save(new Developer("Ana"));
		Developer beto = this.developerRepository.save(new Developer("Beto"));
		this.portal = this.projectRepository.save(new Project("Portal"));
		Project api = this.projectRepository.save(new Project("API"));

		this.assignmentRepository.save(new Assignment(this.ana, this.portal, "tech-lead", LocalDate.of(2026, 1, 10)));
		this.assignmentRepository.save(new Assignment(this.ana, api, "dev", LocalDate.of(2026, 3, 1)));
		this.assignmentRepository.save(new Assignment(beto, this.portal, "dev", LocalDate.of(2026, 2, 5)));
		this.em.flush();
		this.em.clear();
	}

	@Test
	@DisplayName("a chave composta e derivada das duas associacoes")
	void compositeKeyDerivedFromAssociations() {
		Assignment assignment = this.assignmentRepository
				.findById(new AssignmentId(this.ana.getId(), this.portal.getId()))
				.orElseThrow();

		assertThat(assignment.getDeveloper().getName()).isEqualTo("Ana");
		assertThat(assignment.getProject().getTitle()).isEqualTo("Portal");
	}

	@Test
	@DisplayName("as colunas extras (role, assignedAt) ficam na associacao")
	void extraColumnsLiveOnAssociation() {
		assertThat(this.assignmentRepository.findByRole("tech-lead"))
				.singleElement()
				.satisfies(a -> {
					assertThat(a.getDeveloper().getName()).isEqualTo("Ana");
					assertThat(a.getAssignedAt()).isEqualTo(LocalDate.of(2026, 1, 10));
				});
	}

	@Test
	@DisplayName("navegacao nos dois sentidos via query methods")
	void queryFromBothSides() {
		assertThat(this.assignmentRepository.findByDeveloperName("Ana"))
				.extracting(a -> a.getProject().getTitle())
				.containsExactlyInAnyOrder("Portal", "API");

		assertThat(this.assignmentRepository.findByProjectTitle("Portal"))
				.extracting(a -> a.getDeveloper().getName())
				.containsExactlyInAnyOrder("Ana", "Beto");
	}

	@Test
	@DisplayName("remover a associacao nao apaga developer nem project")
	void deletingAssignmentKeepsBothSides() {
		this.assignmentRepository.deleteById(new AssignmentId(this.ana.getId(), this.portal.getId()));
		this.em.flush();

		assertThat(this.assignmentRepository.count()).isEqualTo(2);
		assertThat(this.developerRepository.count()).isEqualTo(2);
		assertThat(this.projectRepository.count()).isEqualTo(2);
	}
}
