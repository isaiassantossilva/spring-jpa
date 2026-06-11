package com.santos.spring_jpa.ids;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidade com chave primaria composta via @EmbeddedId. */
@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
public class Enrollment {

	@EmbeddedId
	private EnrollmentId id;

	private Double grade;

	public Enrollment(EnrollmentId id, Double grade) {
		this.id = id;
		this.grade = grade;
	}
}
