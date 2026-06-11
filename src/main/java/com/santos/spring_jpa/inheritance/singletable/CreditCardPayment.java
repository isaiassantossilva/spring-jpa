package com.santos.spring_jpa.inheritance.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CARD")
@Getter
@Setter
@NoArgsConstructor
public class CreditCardPayment extends Payment {

	private String cardLastDigits;
	private Integer installments;

	public CreditCardPayment(BigDecimal amount, String cardLastDigits, Integer installments) {
		super(amount);
		this.cardLastDigits = cardLastDigits;
		this.installments = installments;
	}
}
