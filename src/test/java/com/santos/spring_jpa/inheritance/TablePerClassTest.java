package com.santos.spring_jpa.inheritance;

import com.santos.spring_jpa.inheritance.tableperclass.Circle;
import com.santos.spring_jpa.inheritance.tableperclass.Shape;
import com.santos.spring_jpa.inheritance.tableperclass.ShapeRepository;
import com.santos.spring_jpa.inheritance.tableperclass.Square;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Heranca TABLE_PER_CLASS: tabela so nas classes concretas, consulta via UNION. */
@DataJpaTest
class TablePerClassTest {

	@Autowired
	private ShapeRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("consulta polimorfica junta as tabelas das subclasses (UNION ALL)")
	void polymorphicQueryAcrossTables() {
		this.repository.save(new Circle("red", 2.0));
		this.repository.save(new Square("blue", 3.0));
		this.em.flush();
		this.em.clear();

		List<Shape> all = this.repository.findAll();

		assertThat(all).hasSize(2);
		assertThat(all).hasAtLeastOneElementOfType(Circle.class);
		assertThat(all).hasAtLeastOneElementOfType(Square.class);
		// ids unicos na hierarquia inteira (sequence compartilhada)
		assertThat(all.stream().map(Shape::getId).distinct()).hasSize(2);
	}

	@Test
	@DisplayName("a classe abstrata nao tem tabela; cada concreta tem a sua")
	void abstractClassHasNoTable() {
		this.repository.save(new Circle("green", 1.0));
		this.em.flush();

		Number shapeTables = (Number) this.em.getEntityManager()
				.createNativeQuery("select count(*) from information_schema.tables "
						+ "where table_name = 'SHAPE' or table_name = 'SHAPES'")
				.getSingleResult();
		Number circles = (Number) this.em.getEntityManager()
				.createNativeQuery("select count(*) from circles")
				.getSingleResult();

		assertThat(shapeTables.intValue()).isZero();
		assertThat(circles.intValue()).isEqualTo(1);
	}
}
