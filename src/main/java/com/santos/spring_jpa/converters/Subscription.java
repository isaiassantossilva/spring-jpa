package com.santos.spring_jpa.converters;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String customer;

	/** Convertido automaticamente (autoApply = true no converter). */
	@Column(length = 1)
	private SubscriptionTier tier;

	/** Converter aplicado explicitamente com @Convert. */
	@Convert(converter = StringListConverter.class)
	@Column(name = "features")
	private List<String> features = new ArrayList<>();

	public Subscription(String customer, SubscriptionTier tier, List<String> features) {
		this.customer = customer;
		this.tier = tier;
		this.features = new ArrayList<>(features);
	}
}
