package com.santos.spring_jpa.locking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Lock otimista com @Version: o Hibernate inclui "where version = ?" em cada
 * UPDATE e lanca OptimisticLockException se outra transacao alterou a linha.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String owner;

	private BigDecimal balance;

	@Version
	private Long version;

	public Account(String owner, BigDecimal balance) {
		this.owner = owner;
		this.balance = balance;
	}
}
