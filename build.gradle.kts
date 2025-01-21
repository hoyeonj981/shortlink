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

		property("sonar.coverage.jacoco.xmlReportPaths",
				layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
		property("sonar.java.checkstyle.reportPaths",
				layout.buildDirectory.file("reports/checkstyle/main.xml"))
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
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
}

tasks.withType<Checkstyle>().configureEach {
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		html.required.set(true)
	}

	classDirectories.setFrom(
			files(classDirectories.files.map {
				fileTree(it) {
					exclude(
							"${project.projectDir}/src/main/java/me/hoyeon/shortlink/ShortlinkApplication.java"
					)
				}
			})
	)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			// 전체 라인 커버리지
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.80".toBigDecimal()  // 최소 80%
			}

			// 분기 커버리지 검증
			limit {
				counter = "BRANCH"
				value = "COVEREDRATIO"
				minimum = "0.70".toBigDecimal()  // 최소 70%
			}
		}
	}
}

tasks.sonar {
	dependsOn(tasks.test, tasks.jacocoTestReport, tasks.checkstyleMain, tasks.checkstyleTest)
}