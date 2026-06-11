package com.santos.spring_jpa.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

	List<LedgerEntry> findByDescriptionStartingWith(String prefix);
}
