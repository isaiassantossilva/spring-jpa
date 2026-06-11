package com.santos.spring_jpa.relationships.associationentity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** Chave composta da entidade associativa (developer_id + project_id). */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AssignmentId implements Serializable {

	private Long developerId;
	private Long projectId;
}
