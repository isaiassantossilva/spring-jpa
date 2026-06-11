package com.santos.spring_jpa.inheritance.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PIX")
@Getter
@Setter
@NoArgsConstructor
public class PixPayment extends Payment {

	private String pixKey;

	public PixPayment(BigDecimal amount, String pixKey) {
		super(amount);
		this.pixKey = pixKey;
	}
}
