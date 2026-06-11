package com.santos.spring_jpa.hibernatefeatures;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

/**
 * @SoftDelete: delete vira UPDATE deleted = true; leituras filtram
 * automaticamente. A linha permanece no banco (visivel so por SQL nativo).
 */
@Entity
@Table(name = "soft_notes")
@SoftDelete
@Getter
@Setter
@NoArgsConstructor
public class SoftNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String text;

	public SoftNote(String text) {
		this.text = text;
	}
}
