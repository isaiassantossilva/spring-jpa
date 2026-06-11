package com.santos.spring_jpa.inheritance;

import com.santos.spring_jpa.inheritance.joined.Car;
import com.santos.spring_jpa.inheritance.joined.Truck;
import com.santos.spring_jpa.inheritance.joined.Vehicle;
import com.santos.spring_jpa.inheritance.joined.VehicleRepository;
import com.santos.spring_jpa.inheritance.singletable.CreditCardPayment;
import com.santos.spring_jpa.inheritance.singletable.CreditCardPaymentRepository;
import com.santos.spring_jpa.inheritance.singletable.Payment;
import com.santos.spring_jpa.inheritance.singletable.PaymentRepository;
import com.santos.spring_jpa.inheritance.singletable.PixPayment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Estrategias de heranca: SINGLE_TABLE e JOINED. */
@DataJpaTest
class InheritanceTest {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CreditCardPaymentRepository creditCardPaymentRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("SINGLE_TABLE: consulta polimorfica retorna todas as subclasses")
	void singleTablePolymorphicQuery() {
		this.paymentRepository.save(new CreditCardPayment(new BigDecimal("250.00"), "1234", 3));
		this.paymentRepository.save(new PixPayment(new BigDecimal("99.90"), "zah@pix.com"));
		this.em.flush();
		this.em.clear();

		List<Payment> all = this.paymentRepository.findAll();
		assertThat(all).hasSize(2);
		assertThat(all).hasAtLeastOneElementOfType(CreditCardPayment.class);
		assertThat(all).hasAtLeastOneElementOfType(PixPayment.class);
	}

	@Test
	@DisplayName("SINGLE_TABLE: repositorio da subclasse filtra pelo discriminador")
	void singleTableSubclassRepository() {
		this.paymentRepository.save(new CreditCardPayment(new BigDecimal("250.00"), "1234", 3));
		this.paymentRepository.save(new PixPayment(new BigDecimal("99.90"), "zah@pix.com"));

		assertThat(this.creditCardPaymentRepository.findAll())
				.singleElement()
				.extracting(CreditCardPayment::getCardLastDigits).isEqualTo("1234");
	}

	@Test
	@DisplayName("SINGLE_TABLE: tudo fica numa unica tabela (payments)")
	void singleTableHasOneTable() {
		this.paymentRepository.save(new PixPayment(new BigDecimal("10.00"), "k"));
		this.em.flush();

		Number rows = (Number) this.em.getEntityManager()
				.createNativeQuery("select count(*) from payments")
				.getSingleResult();
		assertThat(rows.longValue()).isEqualTo(1);
	}

	@Test
	@DisplayName("JOINED: cada classe tem sua tabela, ligadas pela PK")
	void joinedStrategy() {
		this.vehicleRepository.save(new Car("Fiat", 4));
		this.vehicleRepository.save(new Truck("Volvo", 20.0));
		this.em.flush();
		this.em.clear();

		List<Vehicle> all = this.vehicleRepository.findAll();
		assertThat(all).hasSize(2);
		assertThat(all).hasAtLeastOneElementOfType(Car.class);
		assertThat(all).hasAtLeastOneElementOfType(Truck.class);

		Number baseRows = (Number) this.em.getEntityManager()
				.createNativeQuery("select count(*) from vehicles").getSingleResult();
		Number carRows = (Number) this.em.getEntityManager()
				.createNativeQuery("select count(*) from cars").getSingleResult();
		assertThat(baseRows.longValue()).isEqualTo(2);
		assertThat(carRows.longValue()).isEqualTo(1);
	}
}
