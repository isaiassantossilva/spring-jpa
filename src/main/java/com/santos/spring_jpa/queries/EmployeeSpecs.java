package com.santos.spring_jpa.queries;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Specifications: predicados da Criteria API reutilizaveis e combinaveis
 * com and/or/not — ideal para filtros dinamicos (telas de busca).
 */
public final class EmployeeSpecs {

	private EmployeeSpecs() {
	}

	public static Specification<Employee> hasDepartment(String department) {
		return (root, query, cb) -> cb.equal(root.get("department"), department);
	}

	public static Specification<Employee> salaryAtLeast(BigDecimal min) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salary"), min);
	}

	public static Specification<Employee> isActive() {
		return (root, query, cb) -> cb.isTrue(root.get("active"));
	}

	public static Specification<Employee> nameLike(String fragment) {
		return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + fragment.toLowerCase() + "%");
	}
}
