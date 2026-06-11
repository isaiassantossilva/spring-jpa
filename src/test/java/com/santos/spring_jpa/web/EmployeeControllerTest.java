package com.santos.spring_jpa.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice web: @WebMvcTest sobe so o MVC (controller, conversao JSON,
 * validacao); o servico e um mock (@MockitoBean) — sem JPA nem banco.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private EmployeeApiService service;

	private final EmployeeResponse alice = new EmployeeResponse(
			1L, "Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15), true);

	@Test
	@DisplayName("GET /api/employees/{id} retorna 200 com o JSON do funcionario")
	void getByIdReturnsJson() throws Exception {
		given(service.get(1L)).willReturn(alice);

		mvc.perform(get("/api/employees/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Alice"))
				.andExpect(jsonPath("$.email").value("alice@corp.com"));
	}

	@Test
	@DisplayName("GET de id inexistente retorna 404")
	void getMissingReturns404() throws Exception {
		given(service.get(99L)).willThrow(
				new ResponseStatusException(HttpStatus.NOT_FOUND, "funcionario 99 nao existe"));

		mvc.perform(get("/api/employees/99"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("POST valido retorna 201 com Location")
	void postReturns201WithLocation() throws Exception {
		given(service.create(any())).willReturn(alice);

		mvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name": "Alice", "email": "alice@corp.com",
								 "department": "IT", "salary": 9000.00, "hireDate": "2020-01-15"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/employees/1"))
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	@DisplayName("POST invalido e barrado pela Bean Validation com 400, sem chegar ao servico")
	void postInvalidReturns400() throws Exception {
		mvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name": "", "email": "nao-e-email", "salary": -1}
								"""))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(service);
	}
}
