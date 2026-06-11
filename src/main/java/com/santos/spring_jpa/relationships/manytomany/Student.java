package com.santos.spring_jpa.relationships.manytomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @ManyToMany: lado dono define a @JoinTable (tabela associativa
 * student_courses com as duas FKs). Sem cascade REMOVE — remover a
 * associacao nao apaga o curso.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToMany
	@JoinTable(name = "student_courses",
			joinColumns = @JoinColumn(name = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> courses = new HashSet<>();

	public Student(String name) {
		this.name = name;
	}

	public void enroll(Course course) {
		courses.add(course);
		course.getStudents().add(this);
	}

	public void unenroll(Course course) {
		courses.remove(course);
		course.getStudents().remove(this);
	}
}
