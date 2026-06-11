package com.santos.spring_jpa.relationships.associationentity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, AssignmentId> {

	List<Assignment> findByDeveloperName(String name);

	List<Assignment> findByProjectTitle(String title);

	List<Assignment> findByRole(String role);
}
