package com.santos.spring_jpa.hibernatefeatures;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftNoteRepository extends JpaRepository<SoftNote, Long> {
}
