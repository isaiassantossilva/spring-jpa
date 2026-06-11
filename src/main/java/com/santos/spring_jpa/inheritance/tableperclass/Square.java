package com.santos.spring_jpa.inheritance.tableperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "squares")
@Getter
@Setter
@NoArgsConstructor
public class Square extends Shape {

	private double side;

	public Square(String color, double side) {
		super(color);
		this.side = side;
	}
}
