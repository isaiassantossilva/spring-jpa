package com.santos.spring_jpa.relationships.onetoone;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

	/** Navega no relacionamento: where passport.number = ? */
	Optional<Person> findByPassportNumber(String number);
}
