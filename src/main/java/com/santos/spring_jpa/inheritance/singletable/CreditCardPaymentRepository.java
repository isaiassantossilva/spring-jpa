package com.santos.spring_jpa.inheritance.singletable;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio de uma subclasse: as queries filtram pelo discriminador. */
public interface CreditCardPaymentRepository extends JpaRepository<CreditCardPayment, Long> {
}
