package com.santos.spring_jpa.web;

import com.santos.spring_jpa.queries.Employee;
import com.santos.spring_jpa.queries.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Camada de servico entre o controller e o repositorio: dona da transacao
 * e da conversao entidade <-> DTO.
 */
@Service
@RequiredArgsConstructor
public class EmployeeApiService {

	private final EmployeeRepository repository;

	@Transactional(readOnly = true)
	public Page<EmployeeResponse> list(Pageable pageable) {
		return this.repository.findAll(pageable).map(EmployeeResponse::from);
	}

	@Transactional(readOnly = true)
	public EmployeeResponse get(Long id) {
		return this.repository.findById(id)
				.map(EmployeeResponse::from)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "funcionario %d nao existe".formatted(id)));
	}

	@Transactional
	public EmployeeResponse create(EmployeeRequest request) {
		Employee saved = this.repository.save(new Employee(
				request.name(), request.email(), request.department(),
				request.salary(), request.hireDate()));
		return EmployeeResponse.from(saved);
	}
}
