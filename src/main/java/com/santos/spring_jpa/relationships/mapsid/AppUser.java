package com.santos.spring_jpa.relationships.mapsid;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lado pai do @OneToOne com chave derivada (@MapsId): o perfil compartilha
 * a PK do usuario em vez de ter uma FK separada + PK propria.
 */
@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private UserProfile profile;

	public AppUser(String username) {
		this.username = username;
	}

	public void setProfileLinked(UserProfile profile) {
		if (profile != null) {
			profile.setUser(this);
		}
		this.profile = profile;
	}
}
