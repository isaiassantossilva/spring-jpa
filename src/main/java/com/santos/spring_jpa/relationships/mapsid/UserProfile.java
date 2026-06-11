package com.santos.spring_jpa.relationships.mapsid;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @MapsId: o @Id desta entidade NAO tem @GeneratedValue — ele e derivado da
 * associacao. A coluna user_id e PK e FK ao mesmo tempo (shared primary key).
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

	@Id
	private Long id;

	private String bio;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private AppUser user;

	public UserProfile(String bio) {
		this.bio = bio;
	}
}
