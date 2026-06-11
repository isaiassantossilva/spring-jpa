package com.santos.spring_jpa.transactions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Semantica do @Transactional na camada de servico. O teste desliga a
 * transacao do @DataJpaTest (NOT_SUPPORTED): cada chamada de servico abre e
 * fecha a SUA transacao, como em producao — e da para observar o que de
 * fato foi commitado.
 */
@DataJpaTest
@Import({LedgerService.class, AuditTrailService.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TransactionalServiceTest {

	@Autowired
	private LedgerService service;

	@Autowired
	private AuditTrailService auditTrailService;

	@Autowired
	private LedgerEntryRepository repository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@AfterEach
	void cleanUp() {
		repository.deleteAll();
	}

	@Test
	@DisplayName("sucesso = commit")
	void commitsOnSuccess() {
		service.record("compra");

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("RuntimeException = rollback por padrao")
	void rollsBackOnRuntimeException() {
		assertThatThrownBy(() -> service.recordAndFail("compra"))
				.isInstanceOf(IllegalStateException.class);

		assertThat(repository.count()).isZero();
	}

	@Test
	@DisplayName("excecao checked NAO faz rollback por padrao (o save commita!)")
	void checkedExceptionDoesNotRollBackByDefault() {
		assertThatThrownBy(() -> service.recordAndFailChecked("compra"))
				.isInstanceOf(Exception.class);

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("rollbackFor = Exception.class estende o rollback as checked")
	void rollbackForExtendsToChecked() {
		assertThatThrownBy(() -> service.recordAndFailCheckedWithRollback("compra"))
				.isInstanceOf(Exception.class);

		assertThat(repository.count()).isZero();
	}

	@Test
	@DisplayName("noRollbackFor: a excecao listada nao derruba a transacao")
	void noRollbackForKeepsCommit() {
		assertThatThrownBy(() -> service.recordAndFailWithoutRollback("compra"))
				.isInstanceOf(IllegalStateException.class);

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("REQUIRES_NEW (via outro bean) commita e sobrevive ao rollback externo")
	void requiresNewSurvivesOuterRollback() {
		assertThatThrownBy(() -> service.recordWithAuditThenFail("compra"))
				.isInstanceOf(IllegalStateException.class);

		// a entrada principal sumiu; a auditoria (REQUIRES_NEW) ficou
		assertThat(repository.findAll())
				.singleElement()
				.extracting(LedgerEntry::getDescription).isEqualTo("audit: compra");
	}

	@Test
	@DisplayName("capturar a excecao do colaborador REQUIRED nao salva: UnexpectedRollbackException")
	void catchingInnerFailureStillRollsBack() {
		assertThatThrownBy(() -> service.outerCatchesInnerFailure("compra"))
				.isInstanceOf(UnexpectedRollbackException.class);

		assertThat(repository.count()).isZero();
	}

	@Test
	@DisplayName("readOnly = true: alteracao via dirty checking nao e persistida")
	void readOnlySkipsFlush() {
		Long id = service.record("original").getId();

		service.tryToRenameInReadOnly(id, "alterado");

		assertThat(repository.findById(id).orElseThrow().getDescription())
				.isEqualTo("original");
	}

	@Test
	@DisplayName("auto-invocacao ignora o proxy: o REQUIRES_NEW interno nao acontece")
	void selfInvocationBypassesProxy() {
		assertThatThrownBy(() -> service.selfInvocationPitfall("compra"))
				.isInstanceOf(IllegalStateException.class);

		// compare com requiresNewSurvivesOuterRollback: aqui NADA sobrou,
		// porque this.auditViaSelf() rodou na mesma transacao
		assertThat(repository.count()).isZero();
	}

	@Test
	@DisplayName("MANDATORY sem transacao ativa falha")
	void mandatoryRequiresTransaction() {
		assertThatThrownBy(() -> auditTrailService.requiresExistingTransaction())
				.isInstanceOf(IllegalTransactionStateException.class);
	}

	@Test
	@DisplayName("NEVER dentro de transacao ativa falha")
	void neverRejectsActiveTransaction() {
		TransactionTemplate tx = new TransactionTemplate(transactionManager);

		assertThatThrownBy(() -> tx.executeWithoutResult(
				s -> auditTrailService.mustRunWithoutTransaction()))
				.isInstanceOf(IllegalTransactionStateException.class);
	}

	@Test
	@DisplayName("NESTED nao e suportado pelo JpaTransactionManager")
	void nestedIsNotSupportedByJpa() {
		TransactionTemplate tx = new TransactionTemplate(transactionManager);

		assertThatThrownBy(() -> tx.executeWithoutResult(
				s -> auditTrailService.nestedSavepoint()))
				.isInstanceOf(NestedTransactionNotSupportedException.class);
	}
}
