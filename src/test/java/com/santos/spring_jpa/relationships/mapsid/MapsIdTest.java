package com.santos.spring_jpa.relationships.mapsid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/** @OneToOne com chave derivada (@MapsId / shared primary key). */
@DataJpaTest
class MapsIdTest {

	@Autowired
	private AppUserRepository userRepository;

	@Autowired
	private UserProfileRepository profileRepository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("o perfil herda a PK do usuario (PK = FK)")
	void profileSharesPrimaryKey() {
		AppUser user = new AppUser("zah");
		user.setProfileLinked(new UserProfile("Estudando JPA"));
		this.userRepository.saveAndFlush(user);

		assertThat(user.getProfile().getId()).isEqualTo(user.getId());
	}

	@Test
	@DisplayName("da para buscar o perfil direto pelo id do usuario, sem join")
	void findProfileByUserId() {
		AppUser user = new AppUser("maria");
		user.setProfileLinked(new UserProfile("Backend dev"));
		this.userRepository.saveAndFlush(user);
		this.em.clear();

		UserProfile profile = this.profileRepository.findById(user.getId()).orElseThrow();

		assertThat(profile.getBio()).isEqualTo("Backend dev");
		assertThat(profile.getUser().getUsername()).isEqualTo("maria");
	}

	@Test
	@DisplayName("a tabela user_profiles nao tem coluna de PK separada")
	void tableHasSingleKeyColumn() {
		AppUser user = new AppUser("solo");
		user.setProfileLinked(new UserProfile("bio"));
		this.userRepository.saveAndFlush(user);

		Number columns = (Number) this.em.getEntityManager()
				.createNativeQuery("select count(*) from information_schema.columns "
						+ "where table_name = 'USER_PROFILES'")
				.getSingleResult();

		// apenas user_id (PK/FK) e bio
		assertThat(columns.intValue()).isEqualTo(2);
	}
}
