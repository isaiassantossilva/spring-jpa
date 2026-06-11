package com.santos.spring_jpa.basics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * CRUD completo herdado de JpaRepository: save, saveAll, findById, findAll,
 * existsById, count, delete, deleteById, deleteAll...
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findBySku(String sku);
}
