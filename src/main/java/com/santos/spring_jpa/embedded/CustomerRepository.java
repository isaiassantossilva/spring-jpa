package com.santos.spring_jpa.embedded;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	/** Query method navegando em propriedade do embeddable. */
	List<Customer> findByHomeAddressCity(String city);
}
