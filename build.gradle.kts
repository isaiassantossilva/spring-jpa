plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.santos"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	// cache de segundo nivel: Hibernate -> JCache -> EhCache (variante jakarta)
	implementation("org.hibernate.orm:hibernate-jcache")
	implementation("org.ehcache:ehcache::jakarta")
	// QueryDSL: queries type-safe via Q-classes geradas pelo annotation processor
	implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
	// Flyway: migracoes versionadas de schema (desligado por padrao; ver application.yaml).
	// No Boot 4 a autoconfiguracao do Flyway vive no modulo spring-boot-flyway.
	implementation("org.springframework.boot:spring-boot-flyway")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")
	// Bean Validation para os DTOs da camada web
	implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	// 2.4.240 tem bug com check constraints avaliadas em outra conexao (h2database#4320)
	runtimeOnly("com.h2database:h2") {
		version { strictly("2.3.232") }
	}
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	// Testcontainers: banco real (PostgreSQL) em container nos testes
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
