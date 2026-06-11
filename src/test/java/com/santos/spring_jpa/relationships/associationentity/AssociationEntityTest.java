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
		ana = developerRepository.save(new Developer("Ana"));
		Developer beto = developerRepository.save(new Developer("Beto"));
		portal = projectRepository.save(new Project("Portal"));
		Project api = projectRepository.save(new Project("API"));

		assignmentRepository.save(new Assignment(ana, portal, "tech-lead", LocalDate.of(2026, 1, 10)));
		assignmentRepository.save(new Assignment(ana, api, "dev", LocalDate.of(2026, 3, 1)));
		assignmentRepository.save(new Assignment(beto, portal, "dev", LocalDate.of(2026, 2, 5)));
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("a chave composta e derivada das duas associacoes")
	void compositeKeyDerivedFromAssociations() {
		Assignment assignment = assignmentRepository
				.findById(new AssignmentId(ana.getId(), portal.getId()))
				.orElseThrow();

		assertThat(assignment.getDeveloper().getName()).isEqualTo("Ana");
		assertThat(assignment.getProject().getTitle()).isEqualTo("Portal");
	}

	@Test
	@DisplayName("as colunas extras (role, assignedAt) ficam na associacao")
	void extraColumnsLiveOnAssociation() {
		assertThat(assignmentRepository.findByRole("tech-lead"))
				.singleElement()
				.satisfies(a -> {
					assertThat(a.getDeveloper().getName()).isEqualTo("Ana");
					assertThat(a.getAssignedAt()).isEqualTo(LocalDate.of(2026, 1, 10));
				});
	}

	@Test
	@DisplayName("navegacao nos dois sentidos via query methods")
	void queryFromBothSides() {
		assertThat(assignmentRepository.findByDeveloperName("Ana"))
				.extracting(a -> a.getProject().getTitle())
				.containsExactlyInAnyOrder("Portal", "API");

		assertThat(assignmentRepository.findByProjectTitle("Portal"))
				.extracting(a -> a.getDeveloper().getName())
				.containsExactlyInAnyOrder("Ana", "Beto");
	}

	@Test
	@DisplayName("remover a associacao nao apaga developer nem project")
	void deletingAssignmentKeepsBothSides() {
		assignmentRepository.deleteById(new AssignmentId(ana.getId(), portal.getId()));
		em.flush();

		assertThat(assignmentRepository.count()).isEqualTo(2);
		assertThat(developerRepository.count()).isEqualTo(2);
		assertThat(projectRepository.count()).isEqualTo(2);
	}
}
