package com.santos.spring_jpa.queries;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Catalogo de estilos de consulta do Spring Data JPA.
 * JpaSpecificationExecutor habilita consultas dinamicas com a Criteria API.
 */
public interface EmployeeRepository
		extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>,
		QuerydslPredicateExecutor<Employee>, EmployeeRepositoryCustom {

	// ---------- Query methods derivados do nome ----------

	Optional<Employee> findByEmail(String email);

	List<Employee> findByDepartment(String department);

	List<Employee> findByNameContainingIgnoreCase(String fragment);

	List<Employee> findByNameStartingWith(String prefix);

	List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max);

	List<Employee> findBySalaryGreaterThanEqual(BigDecimal min);

	List<Employee> findByDepartmentIn(Collection<String> departments);

	List<Employee> findByActiveTrue();

	List<Employee> findByActiveFalse();

	List<Employee> findByHireDateAfter(LocalDate date);

	List<Employee> findByDepartmentAndActiveTrue(String department);

	List<Employee> findByDepartmentOrDepartment(String first, String second);

	List<Employee> findTop3ByOrderBySalaryDesc();

	Optional<Employee> findFirstByDepartmentOrderByHireDateAsc(String department);

	long countByDepartment(String department);

	boolean existsByEmail(String email);

	/** Derived delete: requer transacao; retorna quantos registros removeu. */
	long deleteByActiveFalse();

	// ---------- Ordenacao e paginacao ----------

	List<Employee> findByActiveTrue(Sort sort);

	Page<Employee> findByActiveTrue(Pageable pageable);

	/** Slice nao executa o count extra — so sabe se ha proxima pagina. */
	Slice<Employee> findBySalaryGreaterThan(BigDecimal min, Pageable pageable);

	// ---------- @Query: JPQL ----------

	@Query("select e from Employee e where e.salary > :min and e.active = true")
	List<Employee> findWellPaidActive(@Param("min") BigDecimal min);

	/** Parametros posicionais. */
	@Query("select e from Employee e where e.department = ?1 order by e.name")
	List<Employee> findByDepartmentSorted(String department);

	/** Funcao de agregacao (avg em JPQL retorna Double). */
	@Query("select avg(e.salary) from Employee e where e.department = :dept")
	Optional<Double> averageSalaryByDepartment(@Param("dept") String dept);

	/** Constructor expression: projeta direto para um DTO. */
	@Query("select new com.santos.spring_jpa.queries.EmployeeSummary(e.name, e.salary) from Employee e where e.department = :dept")
	List<EmployeeSummary> findSummariesByDepartment(@Param("dept") String dept);

	// ---------- @Query: SQL nativo ----------

	@Query(value = "select * from employees where email like '%' || :domain", nativeQuery = true)
	List<Employee> findByEmailDomain(@Param("domain") String domain);

	// ---------- @Modifying: bulk update/delete ----------

	@Modifying(clearAutomatically = true)
	@Query("update Employee e set e.salary = e.salary * :factor where e.department = :dept")
	int raiseSalaryByDepartment(@Param("dept") String dept, @Param("factor") BigDecimal factor);

	@Modifying
	@Query("delete from Employee e where e.active = false")
	int purgeInactive();

	// ---------- Named queries (definidas na entidade Employee) ----------

	/** Resolvida pela named query "Employee.findEarningMoreThan". */
	List<Employee> findEarningMoreThan(@Param("salary") BigDecimal salary);

	/** Named native query + @SqlResultSetMapping projetando para o record. */
	List<EmployeeSummary> findSummariesNative(@Param("dept") String dept);

	// ---------- Stream, Limit e scroll (keyset/offset) ----------

	/** Consumir com try-with-resources, dentro de uma transacao. */
	@Query("select e from Employee e where e.active = true")
	Stream<Employee> streamActive();

	/** Limit dinamico, sem fixar Top-N no nome do metodo. */
	List<Employee> findByActiveTrueOrderBySalaryDesc(Limit limit);

	/** Scroll API: pagina por posicao (keyset ou offset) em vez de Page. */
	Window<Employee> findTop2ByActiveTrueOrderBySalaryDescIdAsc(ScrollPosition position);

	// ---------- Stored procedure / funcao do banco ----------

	/**
	 * Em bancos com procedures de verdade (Postgres, MySQL...) usa-se
	 * {@code @Procedure(procedureName = "plus_tax")}. O H2 nao suporta
	 * parametros OUT — suas "procedures" sao funcoes Java registradas com
	 * CREATE ALIAS — entao a chamada e feita com CALL nativo.
	 */
	@Query(value = "call plus_tax(:amount)", nativeQuery = true)
	Double plusTax(@Param("amount") Double amount);

	// ---------- Projecoes ----------

	List<EmployeeNameOnly> findByDepartmentOrderByName(String department);

	/** Projecao dinamica: o chamador escolhe o tipo de retorno. */
	<T> List<T> findByActiveTrue(Class<T> type);
}
