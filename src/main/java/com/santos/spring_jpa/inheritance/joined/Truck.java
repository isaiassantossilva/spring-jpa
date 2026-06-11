package com.santos.spring_jpa.inheritance.joined;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trucks")
@Getter
@Setter
@NoArgsConstructor
public class Truck extends Vehicle {

	@Column(nullable = false)
	private Double payloadTons;

	public Truck(String brand, Double payloadTons) {
		super(brand);
		this.payloadTons = payloadTons;
	}
}
