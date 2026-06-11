package com.santos.spring_jpa.ids;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryLanguageRepository extends JpaRepository<CountryLanguage, CountryLanguageId> {

	List<CountryLanguage> findByCountry(String country);
}
