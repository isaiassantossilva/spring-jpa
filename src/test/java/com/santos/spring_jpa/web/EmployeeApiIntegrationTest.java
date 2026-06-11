package com.santos.spring_jpa.web;

import com.santos.spring_jpa.queries.Employee;
import com.santos.spring_jpa.queries.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracao de ponta a ponta: contexto completo (@SpringBootTest) com
 * MockMvc — HTTP -> controller -> servico -> repositorio -> H2 e de volta.
 * @Transactional faz rollback ao final de cada teste.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeApiIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private EmployeeRepository repository;

	@Test
	@DisplayName("POST cria no banco e o GET do Location devolve o recurso")
	void createThenFetch() throws Exception {
		String location = this.mvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name": "Erin", "email": "erin@corp.com",
								 "department": "Sales", "salary": 6000.00, "hireDate": "2023-02-20"}
								"""))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getHeader("Location");

		this.mvc.perform(get(location))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Erin"))
				.andExpect(jsonPath("$.department").value("Sales"));

		assertThat(this.repository.findByEmail("erin@corp.com")).isPresent();
	}

	@Test
	@DisplayName("paginacao via query params chega ao repositorio e volta como JSON")
	void paginationEndToEnd() throws Exception {
		this.repository.save(new Employee("Alice", "alice@corp.com", "IT", new BigDecimal("9000.00"), LocalDate.of(2020, 1, 15)));
		this.repository.save(new Employee("Bob", "bob@corp.com", "IT", new BigDecimal("5000.00"), LocalDate.of(2021, 3, 10)));
		this.repository.save(new Employee("Carol", "carol@corp.com", "HR", new BigDecimal("7000.00"), LocalDate.of(2019, 7, 1)));

		this.mvc.perform(get("/api/employees")
						.param("page", "0")
						.param("size", "2")
						.param("sort", "salary,desc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].name").value("Alice"))
				.andExpect(jsonPath("$.content[1].name").value("Carol"))
				.andExpect(jsonPath("$.page.totalElements").value(3))
				.andExpect(jsonPath("$.page.totalPages").value(2));
	}
}
