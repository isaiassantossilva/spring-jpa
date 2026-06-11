package com.santos.spring_jpa.queries;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade usada pelos exemplos de consultas, paginacao, projecoes etc.
 *
 * Named queries: definidas na entidade e encontradas pelo Spring Data pela
 * convencao "<Entidade>.<nomeDoMetodo>" — sem precisar de @Query no repositorio.
 */
@NamedQuery(name = "Employee.findEarningMoreThan",
		query = "select e from Employee e where e.salary > :salary order by e.salary desc")
@NamedNativeQuery(name = "Employee.findSummariesNative",
		query = "select name, salary from employees where department = :dept",
		resultSetMapping = "EmployeeSummaryMapping")
@SqlResultSetMapping(name = "EmployeeSummaryMapping",
		classes = @ConstructorResult(targetClass = EmployeeSummary.class, columns = {
				@ColumnResult(name = "name"),
				@ColumnResult(name = "salary", type = BigDecimal.class)
		}))
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true)
	private String email;

	private String department;

	@Column(precision = 12, scale = 2)
	private BigDecimal salary;

	private LocalDate hireDate;

	private boolean active = true;

	public Employee(String name, String email, String department, BigDecimal salary, LocalDate hireDate) {
		this.name = name;
		this.email = email;
		this.department = department;
		this.salary = salary;
		this.hireDate = hireDate;
	}
}
