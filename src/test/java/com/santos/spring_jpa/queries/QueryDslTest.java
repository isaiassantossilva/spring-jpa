package com.santos.spring_jpa.queries;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.santos.spring_jpa.queries.QEmployee.employee;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * QueryDSL: predicados type-safe sobre as Q-classes geradas na compilacao
 * (QEmployee). Errar o nome de um campo vira erro de compilacao, nao
 * QuerySyntaxException em runtime.
 */
@DataJpaTest
class QueryDslTest {

	@Autowired
	private EmployeeRepository repository;

	@Autowired
	private EntityManager em;

	@BeforeEach
	void seed() {
		this.repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		this.repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		this.repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));
		Employee dave = new Employee("Dave", "dave@corp.com", "HR", new BigDecimal("4000.00"), LocalDate.of(2022, 11, 5));
		dave.setActive(false);
		this.repository.save(dave);
	}

	@Test
	@DisplayName("QuerydslPredicateExecutor: findAll/count/exists com Predicate")
	void predicateExecutor() {
		assertThat(this.repository.findAll(employee.department.eq("IT").and(employee.salary.goe(6000))))
				.extracting(Employee::getName).containsExactly("Alice");

		assertThat(this.repository.findAll(employee.active.isTrue(), Sort.by("salary").descending()))
				.extracting(Employee::getName).containsExactly("Alice", "Carol", "Bob");

		assertThat(this.repository.count(employee.department.eq("HR"))).isEqualTo(2);
		assertThat(this.repository.exists(employee.name.startsWith("Da"))).isTrue();
	}

	@Test
	@DisplayName("JPAQueryFactory: query fluente com where/orderBy")
	void jpaQueryFactory() {
		JPAQueryFactory queryFactory = new JPAQueryFactory(this.em);

		List<Employee> result = queryFactory
				.selectFrom(employee)
				.where(employee.name.containsIgnoreCase("a")
						.and(employee.hireDate.before(LocalDate.of(2022, 1, 1))))
				.orderBy(employee.salary.desc())
				.fetch();

		assertThat(result).extracting(Employee::getName).containsExactly("Alice", "Carol");
	}

	@Test
	@DisplayName("projecao para DTO e agregacao com group by")
	void projectionsAndAggregation() {
		JPAQueryFactory queryFactory = new JPAQueryFactory(this.em);

		List<EmployeeSummary> summaries = queryFactory
				.select(Projections.constructor(EmployeeSummary.class, employee.name, employee.salary))
				.from(employee)
				.where(employee.department.eq("IT"))
				.fetch();
		assertThat(summaries).containsExactlyInAnyOrder(
				new EmployeeSummary("Alice", new BigDecimal("9000.00")),
				new EmployeeSummary("Bob", new BigDecimal("5000.00")));

		List<Tuple> byDepartment = queryFactory
				.select(employee.department, employee.salary.avg())
				.from(employee)
				.groupBy(employee.department)
				.orderBy(employee.department.asc())
				.fetch();
		assertThat(byDepartment).hasSize(2); // HR e IT
		assertThat(byDepartment.getFirst().get(employee.department)).isEqualTo("HR");
		assertThat(byDepartment.getFirst().get(employee.salary.avg())).isEqualTo(5500.0);
	}
}
