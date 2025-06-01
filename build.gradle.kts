plugins {
	java
	checkstyle
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	id("jacoco")
	id("org.sonarqube") version "6.0.1.5171"
}

group = "me.hoyeon"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

checkstyle {
	toolVersion = "10.21.1"

	configFile = file("${rootDir}/.config/checkstyle/checkstyle.xml")

	isIgnoreFailures = false

	maxErrors = 0
	maxWarnings= 0
}

jacoco {
	toolVersion = "0.8.12"
}

sonar {
	properties {
		property("sonar.projectKey", System.getenv("SONAR_PROJECT_KEY") ?: "")
		property("sonar.organization", System.getenv("SONAR_ORGANIZATION") ?: "")
		property("sonar.host.url", System.getenv("SONAR_HOST") ?: "")
		property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")

		property("sonar.coverage.jacoco.xmlReportPaths",
				"${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.java.checkstyle.reportPaths",
				"${buildDir}/reports/checkstyle/main.xml")

		property(
			"sonar.coverage.exclusions",
					"**/me/hoyeon/shortlink/*Application.java," +
					"**/me/hoyeon/shortlink/infrastructure/security/**," +
					"**/*Exception.java," +
					"**/*Properties.java, " +
					"**/Q*.java"
		)
	}
}

sourceSets {
	main {
		java.srcDirs("src/main/java", "build/generated/sources/annotationProcessor/java/main")
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("com.auth0:java-jwt:4.5.0")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")

	implementation("com.maxmind.geoip2:geoip2:4.3.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
	finalizedBy(tasks.jacocoTestReport)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
}

tasks.withType<Checkstyle>().configureEach {
	exclude("**/Q*.java")
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
	}

	classDirectories.setFrom(
			files(classDirectories.files.map {
				fileTree(it) {
					exclude(
						"**/me/hoyeon/shortlink/*Application.class",
						"**/me/hoyeon/shortlink/infrastructure/*Properties.class",
						"**/*Exception.class"
					)
				}
			})
	)
}