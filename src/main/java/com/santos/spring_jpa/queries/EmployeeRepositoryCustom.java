package com.santos.spring_jpa.queries;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fragment de repositorio customizado: declara operacoes que nao dao para
 * expressar como query method e ganha implementacao manual no *Impl.
 */
public interface EmployeeRepositoryCustom {

	/** Busca com filtros opcionais: cada parametro null e ignorado. */
	List<Employee> search(String department, BigDecimal minSalary, Boolean active);
}
