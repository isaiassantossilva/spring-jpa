package com.santos.spring_jpa.transactions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Demonstracoes de @Transactional na camada de servico. O Spring envolve o
 * bean num proxy: a transacao comeca ao entrar no metodo publico e commita
 * (ou faz rollback) ao sair.
 */
@Service
@RequiredArgsConstructor
public class LedgerService {

	private final LedgerEntryRepository repository;
	private final AuditTrailService auditTrailService;

	@Transactional
	public LedgerEntry record(String description) {
		return this.repository.save(new LedgerEntry(description));
	}

	/** RuntimeException faz rollback por padrao: o save abaixo e desfeito. */
	@Transactional
	public void recordAndFail(String description) {
		this.repository.save(new LedgerEntry(description));
		throw new IllegalStateException("falha apos gravar");
	}

	/** Excecao CHECKED nao faz rollback por padrao: o save e commitado! */
	@Transactional
	public void recordAndFailChecked(String description) throws Exception {
		this.repository.save(new LedgerEntry(description));
		throw new Exception("checked nao dispara rollback por padrao");
	}

	/** rollbackFor estende o rollback as checked. */
	@Transactional(rollbackFor = Exception.class)
	public void recordAndFailCheckedWithRollback(String description) throws Exception {
		this.repository.save(new LedgerEntry(description));
		throw new Exception("com rollbackFor, agora desfaz");
	}

	/** noRollbackFor: a excecao listada NAO derruba a transacao. */
	@Transactional(noRollbackFor = IllegalStateException.class)
	public void recordAndFailWithoutRollback(String description) {
		this.repository.save(new LedgerEntry(description));
		throw new IllegalStateException("lancada, mas o save commita");
	}

	/**
	 * A auditoria roda em REQUIRES_NEW (via OUTRO bean, passando pelo proxy):
	 * ela commita antes da falha e sobrevive ao rollback desta transacao.
	 */
	@Transactional
	public void recordWithAuditThenFail(String description) {
		this.auditTrailService.logAttempt(description);
		this.repository.save(new LedgerEntry(description));
		throw new IllegalStateException("rollback so da transacao principal");
	}

	/**
	 * Capturar a excecao do colaborador REQUIRED nao salva a transacao: ela
	 * ja foi marcada rollback-only — o commit no final lanca
	 * UnexpectedRollbackException para quem chamou.
	 */
	@Transactional
	public void outerCatchesInnerFailure(String description) {
		this.repository.save(new LedgerEntry(description));
		try {
			this.auditTrailService.failInCurrentTransaction();
		} catch (IllegalStateException ignored) {
			// engolida — mas a transacao compartilhada ja esta condenada
		}
	}

	/**
	 * readOnly = true: flush mode MANUAL — alteracoes via dirty checking nao
	 * sao enviadas ao banco no commit (alem de otimizacoes do driver/Hibernate).
	 */
	@Transactional(readOnly = true)
	public void tryToRenameInReadOnly(Long id, String newDescription) {
		this.repository.findById(id).orElseThrow().setDescription(newDescription);
	}

	/**
	 * PEGADINHA da auto-invocacao: this.auditViaSelf() NAO passa pelo proxy —
	 * o REQUIRES_NEW dele e ignorado e tudo roda numa transacao so. A falha
	 * no final desfaz inclusive a "auditoria".
	 */
	@Transactional
	public void selfInvocationPitfall(String description) {
		this.auditViaSelf("audit: " + description);
		this.repository.save(new LedgerEntry(description));
		throw new IllegalStateException("derruba tudo, inclusive a auditoria");
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void auditViaSelf(String message) {
		this.repository.save(new LedgerEntry(message));
	}
}
