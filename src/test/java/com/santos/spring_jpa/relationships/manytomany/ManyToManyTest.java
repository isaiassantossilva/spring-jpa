package com.santos.spring_jpa.relationships.manytomany;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** @ManyToMany com tabela associativa (@JoinTable). */
@DataJpaTest
class ManyToManyTest {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private TestEntityManager em;

	private Long aliceId;

	@BeforeEach
	void setUp() {
		Course jpa = courseRepository.save(new Course("JPA"));
		Course spring = courseRepository.save(new Course("Spring"));

		Student alice = new Student("Alice");
		alice.enroll(jpa);
		alice.enroll(spring);
		aliceId = studentRepository.save(alice).getId();

		Student bruno = new Student("Bruno");
		bruno.enroll(jpa);
		studentRepository.save(bruno);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("associacoes sao gravadas na join table e recarregadas")
	void persistsAssociations() {
		Student alice = studentRepository.findById(aliceId).orElseThrow();

		assertThat(alice.getCourses())
				.extracting(Course::getTitle)
				.containsExactlyInAnyOrder("JPA", "Spring");
	}

	@Test
	@DisplayName("query method com join implicito (findByCoursesTitle)")
	void findStudentsByCourse() {
		assertThat(studentRepository.findByCoursesTitle("JPA"))
				.extracting(Student::getName)
				.containsExactlyInAnyOrder("Alice", "Bruno");
	}

	@Test
	@DisplayName("remover a matricula apaga so a linha da join table, nao o curso")
	void removingAssociationKeepsCourse() {
		Student alice = studentRepository.findById(aliceId).orElseThrow();
		Course jpa = alice.getCourses().stream()
				.filter(c -> c.getTitle().equals("JPA"))
				.findFirst().orElseThrow();

		alice.unenroll(jpa);
		em.flush();
		em.clear();

		assertThat(courseRepository.count()).isEqualTo(2);
		assertThat(studentRepository.findById(aliceId).orElseThrow().getCourses())
				.extracting(Course::getTitle)
				.containsExactly("Spring");
	}
}
