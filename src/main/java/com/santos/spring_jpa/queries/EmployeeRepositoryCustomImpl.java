package com.santos.spring_jpa.queries;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao do fragment usando a Criteria API "crua" do JPA.
 * A convencao de nome (interface + "Impl") faz o Spring Data plugar esta
 * classe no proxy do EmployeeRepository automaticamente.
 */
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Employee> search(String department, BigDecimal minSalary, Boolean active) {
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
		Root<Employee> employee = query.from(Employee.class);

		List<Predicate> predicates = new ArrayList<>();
		if (department != null) {
			predicates.add(cb.equal(employee.get("department"), department));
		}
		if (minSalary != null) {
			predicates.add(cb.greaterThanOrEqualTo(employee.get("salary"), minSalary));
		}
		if (active != null) {
			predicates.add(cb.equal(employee.get("active"), active));
		}

		query.select(employee)
				.where(predicates.toArray(Predicate[]::new))
				.orderBy(cb.asc(employee.get("name")));

		return this.em.createQuery(query).getResultList();
	}
}
