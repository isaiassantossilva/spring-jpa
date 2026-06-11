package com.santos.spring_jpa.locking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lock pessimista de verdade exige transacoes CONCORRENTES — por isso este
 * teste desliga a transacao do proprio teste (NOT_SUPPORTED) e abre as suas
 * com TransactionTemplate em threads separadas.
 */
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PessimisticLockingTest {

	@Autowired
	private AccountRepository repository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@AfterEach
	void cleanUp() {
		this.repository.deleteAll();
	}

	@Test
	@DisplayName("SELECT FOR UPDATE serializa read-modify-write concorrente (sem lost update)")
	void concurrentIncrementsAreSerialized() throws Exception {
		TransactionTemplate tx = new TransactionTemplate(this.transactionManager);
		tx.executeWithoutResult(s -> this.repository.save(new Account("concorrente", new BigDecimal("100.00"))));

		int workers = 4;
		try (ExecutorService pool = Executors.newFixedThreadPool(workers)) {
			List<Future<?>> futures = new ArrayList<>();
			for (int i = 0; i < workers; i++) {
				futures.add(pool.submit(() -> tx.executeWithoutResult(s -> {
					// bloqueia a linha ate o commit; os demais workers esperam
					Account account = this.repository.findWithLockByOwner("concorrente").orElseThrow();
					account.setBalance(account.getBalance().add(new BigDecimal("50.00")));
				})));
			}
			for (Future<?> future : futures) {
				future.get(); // propaga qualquer falha das threads
			}
		}

		Account result = tx.execute(s -> this.repository.findWithLockByOwner("concorrente").orElseThrow());
		// 100 + 4 x 50: nenhum incremento foi perdido
		assertThat(result.getBalance()).isEqualByComparingTo("300.00");
	}
}
