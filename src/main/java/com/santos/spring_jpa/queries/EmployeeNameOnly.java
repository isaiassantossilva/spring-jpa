package com.santos.spring_jpa.queries;

import org.springframework.beans.factory.annotation.Value;

/**
 * Projecao por interface (closed projection): so as colunas dos getters sao
 * buscadas. getLabel() e uma open projection calculada via SpEL.
 */
public interface EmployeeNameOnly {

	String getName();

	@Value("#{target.name + ' (' + target.department + ')'}")
	String getLabel();
}
