package com.santos.spring_jpa.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Borda HTTP da paginacao do Spring Data: Pageable e montado a partir dos
 * query params (?page=0&size=2&sort=salary,desc) e PagedModel define um
 * contrato JSON estavel para a pagina.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeApiService service;

	@GetMapping
	public PagedModel<EmployeeResponse> list(Pageable pageable) {
		return new PagedModel<>(this.service.list(pageable));
	}

	@GetMapping("/{id}")
	public EmployeeResponse get(@PathVariable Long id) {
		return this.service.get(id);
	}

	@PostMapping
	public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse created = this.service.create(request);
		return ResponseEntity
				.created(URI.create("/api/employees/" + created.id()))
				.body(created);
	}
}
