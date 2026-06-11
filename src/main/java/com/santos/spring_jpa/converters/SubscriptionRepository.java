package com.santos.spring_jpa.converters;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	/** O parametro tambem passa pelo converter: where tier = 'P'. */
	List<Subscription> findByTier(SubscriptionTier tier);
}
