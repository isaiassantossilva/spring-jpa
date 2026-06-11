package com.santos.spring_jpa.queries;

import java.math.BigDecimal;

/** Projecao DTO baseada em record (class-based projection). */
public record EmployeeSummary(String name, BigDecimal salary) {
}
