package com.santos.spring_jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Habilita o preenchimento automatico de @CreatedDate/@LastModifiedDate e,
 * via AuditorAware, de @CreatedBy/@LastModifiedBy.
 * Em testes @DataJpaTest, importe com @Import(JpaAuditingConfig.class).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

	/**
	 * Fornece o "quem" da auditoria. Numa aplicacao real viria do
	 * SecurityContextHolder (usuario autenticado); aqui e fixo para estudo.
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of("test-user");
	}
}
