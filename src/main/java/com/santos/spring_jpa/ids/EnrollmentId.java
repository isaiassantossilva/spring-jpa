package com.santos.spring_jpa.ids;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Chave composta com @EmbeddedId: a classe precisa ser @Embeddable,
 * Serializable e implementar equals/hashCode.
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EnrollmentId implements Serializable {

	private Long studentId;
	private Long courseId;
}
