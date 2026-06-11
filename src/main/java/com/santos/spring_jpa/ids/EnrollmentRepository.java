package com.santos.spring_jpa.ids;

import org.springframework.data.jpa.repository.JpaRepository;

/** O tipo do ID do repositorio e a propria classe da chave composta. */
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
}
