package com.santos.spring_jpa.transactions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servico colaborador: as chamadas vindas de OUTRO bean passam pelo proxy
 * transacional, entao cada propagacao declarada aqui e respeitada.
 */
@Service
@RequiredArgsConstructor
public class AuditTrailService {

	private final LedgerEntryRepository repository;

	/**
	 * REQUIRES_NEW: suspende a transacao de quem chamou e abre uma propria,
	 * que commita sozinha — sobrevive mesmo se a transacao externa der rollback.
	 * E o padrao classico de trilha de auditoria.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logAttempt(String message) {
		repository.save(new LedgerEntry("audit: " + message));
	}

	/**
	 * REQUIRED (padrao): junta-se a transacao de quem chamou. Ao lancar a
	 * excecao, o proxy marca essa transacao COMPARTILHADA como rollback-only —
	 * mesmo que o chamador capture a excecao, o commit dele vai falhar.
	 */
	@Transactional
	public void failInCurrentTransaction() {
		throw new IllegalStateException("falha dentro da transacao compartilhada");
	}

	/** MANDATORY: exige transacao existente; sem ela, IllegalTransactionStateException. */
	@Transactional(propagation = Propagation.MANDATORY)
	public void requiresExistingTransaction() {
		repository.save(new LedgerEntry("mandatory"));
	}

	/** NEVER: o oposto — falha se HOUVER transacao ativa. */
	@Transactional(propagation = Propagation.NEVER)
	public void mustRunWithoutTransaction() {
	}

	/**
	 * NESTED: savepoint dentro da transacao corrente. Funciona com JDBC puro;
	 * o JpaTransactionManager NAO suporta (lanca NestedTransactionNotSupportedException).
	 */
	@Transactional(propagation = Propagation.NESTED)
	public void nestedSavepoint() {
	}
}
