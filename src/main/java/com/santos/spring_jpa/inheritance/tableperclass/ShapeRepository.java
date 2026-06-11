package com.santos.spring_jpa.inheritance.tableperclass;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShapeRepository extends JpaRepository<Shape, Long> {
}
