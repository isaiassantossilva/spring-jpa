package com.santos.spring_jpa.inheritance.joined;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
public class Car extends Vehicle {

	@Column(nullable = false)
	private Integer doors;

	public Car(String brand, Integer doors) {
		super(brand);
		this.doors = doors;
	}
}
