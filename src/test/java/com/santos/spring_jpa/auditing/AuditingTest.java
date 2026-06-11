package com.santos.spring_jpa.auditing;

import com.santos.spring_jpa.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Auditoria com @CreatedDate/@LastModifiedDate em uma @MappedSuperclass.
 * O slice @DataJpaTest nao carrega @Configuration extras — por isso o @Import.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
class AuditingTest {

	@Autowired
	private TaskItemRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("@CreatedDate e @LastModifiedDate sao preenchidos no insert")
	void populatesAuditFieldsOnInsert() {
		TaskItem task = repository.saveAndFlush(new TaskItem("Estudar JPA"));

		assertThat(task.getCreatedAt()).isNotNull();
		assertThat(task.getUpdatedAt()).isNotNull();
	}

	@Test
	@DisplayName("@CreatedBy/@LastModifiedBy vem do AuditorAware")
	void populatesAuditor() {
		TaskItem task = repository.saveAndFlush(new TaskItem("Estudar auditoria"));

		assertThat(task.getCreatedBy()).isEqualTo("test-user");
		assertThat(task.getUpdatedBy()).isEqualTo("test-user");
	}

	@Test
	@DisplayName("@LastModifiedDate avanca no update; @CreatedDate nao muda")
	void updatesLastModifiedOnUpdate() throws InterruptedException {
		TaskItem task = repository.saveAndFlush(new TaskItem("Estudar JPA"));
		var createdAt = task.getCreatedAt();
		var firstUpdatedAt = task.getUpdatedAt();

		Thread.sleep(50);
		task.setDone(true);
		em.flush();

		assertThat(task.getCreatedAt()).isEqualTo(createdAt);
		assertThat(task.getUpdatedAt()).isAfter(firstUpdatedAt);
	}
}
