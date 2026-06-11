package com.santos.spring_jpa.web;

import com.santos.spring_jpa.queries.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO de saida: controla o contrato JSON, desacoplado do mapeamento JPA. */
public record EmployeeResponse(
		Long id,
		String name,
		String email,
		String department,
		BigDecimal salary,
		LocalDate hireDate,
		boolean active) {

	public static EmployeeResponse from(Employee employee) {
		return new EmployeeResponse(
				employee.getId(),
				employee.getName(),
				employee.getEmail(),
				employee.getDepartment(),
				employee.getSalary(),
				employee.getHireDate(),
				employee.isActive());
	}
}
