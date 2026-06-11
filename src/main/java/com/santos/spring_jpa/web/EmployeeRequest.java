package com.santos.spring_jpa.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada: a entidade JPA nunca cruza a borda HTTP. Bean Validation
 * roda antes do controller (@Valid) e devolve 400 automaticamente.
 */
public record EmployeeRequest(
		@NotBlank String name,
		@NotBlank @Email String email,
		String department,
		@Positive BigDecimal salary,
		@PastOrPresent LocalDate hireDate) {
}
