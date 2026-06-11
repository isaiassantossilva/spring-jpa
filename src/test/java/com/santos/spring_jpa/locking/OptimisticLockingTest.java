package com.santos.spring_jpa.locking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Lock otimista com @Version. */
@DataJpaTest
class OptimisticLockingTest {

	@Autowired
	private AccountRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("@Version inicia em 0 e incrementa a cada update")
	void versionIncrements() {
		Account account = this.repository.saveAndFlush(new Account("zah", new BigDecimal("100.00")));
		assertThat(account.getVersion()).isZero();

		account.setBalance(new BigDecimal("150.00"));
		this.em.flush();

		assertThat(account.getVersion()).isEqualTo(1);
	}

	@Test
	@DisplayName("salvar uma copia desatualizada (stale) lanca OptimisticLockingFailureException")
	void staleUpdateFails() {
		Account account = this.repository.saveAndFlush(new Account("zah", new BigDecimal("100.00")));
		Long id = account.getId();

		// outra "transacao" altera a conta: versao no banco vira 1
		account.setBalance(new BigDecimal("200.00"));
		this.em.flush();
		this.em.clear();

		// copia detached carregada antes da alteracao (versao 0)
		Account stale = new Account("zah", new BigDecimal("50.00"));
		stale.setId(id);
		stale.setVersion(0L);

		assertThatThrownBy(() -> this.repository.saveAndFlush(stale))
				.isInstanceOf(OptimisticLockingFailureException.class);
	}
}
