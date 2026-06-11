package com.santos.spring_jpa.relationships.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

	/** Join implicito pela colecao: alunos matriculados no curso com este titulo. */
	List<Student> findByCoursesTitle(String title);
}
