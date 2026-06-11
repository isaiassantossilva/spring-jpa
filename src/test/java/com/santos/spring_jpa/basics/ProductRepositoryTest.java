package com.santos.spring_jpa.basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRUD basico com JpaRepository + mapeamentos de coluna.
 * @DataJpaTest sobe so a camada JPA com H2 em memoria e roda cada teste
 * dentro de uma transacao com rollback ao final.
 */
@DataJpaTest
class ProductRepositoryTest {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("save persiste e gera o ID (IDENTITY)")
	void saveGeneratesId() {
		Product product = this.repository.save(new Product("Teclado", "SKU-001", new BigDecimal("199.90")));

		assertThat(product.getId()).isNotNull();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
		assertThat(product.getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("findById e findBySku recuperam a entidade")
	void findByIdAndBySku() {
		Product saved = this.em.persistFlushFind(new Product("Mouse", "SKU-002", new BigDecimal("89.90")));

		assertThat(this.repository.findById(saved.getId())).isPresent();
		assertThat(this.repository.findBySku("SKU-002")).isPresent()
				.get().extracting(Product::getName).isEqualTo("Mouse");
		assertThat(this.repository.findBySku("NAO-EXISTE")).isEmpty();
	}

	@Test
	@DisplayName("entidade gerenciada e atualizada via dirty checking, sem chamar save")
	void updateViaDirtyChecking() {
		Product saved = this.em.persistFlushFind(new Product("Monitor", "SKU-003", new BigDecimal("1200.00")));

		saved.setStatus(ProductStatus.ACTIVE);
		saved.setPrice(new BigDecimal("999.99"));
		this.em.flush();
		this.em.clear();

		Product reloaded = this.repository.findById(saved.getId()).orElseThrow();
		assertThat(reloaded.getStatus()).isEqualTo(ProductStatus.ACTIVE);
		assertThat(reloaded.getPrice()).isEqualByComparingTo("999.99");
	}

	@Test
	@DisplayName("campo @Transient nao e persistido")
	void transientFieldIsNotPersisted() {
		Product product = new Product("Cabo", "SKU-004", new BigDecimal("10.00"));
		product.setPriceWithTax(new BigDecimal("12.00"));
		Long id = this.em.persistAndFlush(product).getId();
		this.em.clear();

		Product reloaded = this.repository.findById(id).orElseThrow();
		assertThat(reloaded.getPriceWithTax()).isNull();
	}

	@Test
	@DisplayName("delete, count e existsById")
	void deleteCountExists() {
		Product a = this.repository.save(new Product("A", "SKU-A", BigDecimal.ONE));
		this.repository.save(new Product("B", "SKU-B", BigDecimal.TEN));

		assertThat(this.repository.count()).isEqualTo(2);
		assertThat(this.repository.existsById(a.getId())).isTrue();

		this.repository.delete(a);
		assertThat(this.repository.count()).isEqualTo(1);
		assertThat(this.repository.existsById(a.getId())).isFalse();
	}
}
