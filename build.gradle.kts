plugins {
	java
	checkstyle
	jacoco
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.asciidoctor.jvm)
	alias(libs.plugins.sonarqube)
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
					"**/Q*.java" +
                    "**/*Risk.java" +
                    "**/*Category.java" +
                    "**/RiskLevel.java" +
                    "**/SafetyDecision.java" +
                    "**/ActorInfo.java" +
                    "**/UrlSafetyContext.java" +
                    "**/SafetyDecision.java"
		)
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
	// spring boot
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.validation)
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.mail)
	implementation(libs.spring.boot.starter.oauth2.client)
	developmentOnly(libs.spring.boot.devtools)

	// db
	runtimeOnly(libs.h2)
	runtimeOnly(libs.mysql.connector.j)

	// test
	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.spring.restdocs.mockmvc)
	testImplementation(libs.spring.security.test)
	testImplementation(libs.mockwebserver)
	testImplementation(libs.spring.boot.testcontainers)
	testImplementation(libs.testcontainers.junit.jupiter)
	testImplementation(libs.testcontainers.mysql)
	testRuntimeOnly(libs.junit.platform.launcher)

	// queryDsl
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor(libs.jakarta.persistence.api)
	annotationProcessor(libs.jakarta.annotation.api)

	// external libs
	implementation(libs.java.jwt)
	implementation(libs.geoip2)
	compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)
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