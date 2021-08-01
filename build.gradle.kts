import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "2.5.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
}

group = "uz.viento"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

tasks.getByName<BootBuildImage>("bootBuildImage") {
	if (System.getenv("PUBLISH_DOCKER_IMAGE") == "true") {
		isPublish = true

		val imagePrefix = System.getenv("PUBLISH_DOCKER_IMAGE_PREFIX")
			?: System.getenv("PUBLISH_DOCKER_IMAGE_USERNAME")
		imageName = "$imagePrefix/${project.name}:${project.version}"

		docker {
			publishRegistry {
				username = System.getenv("PUBLISH_DOCKER_IMAGE_USERNAME")
				password = System.getenv("PUBLISH_DOCKER_IMAGE_PASSWORD")
				url = System.getenv("PUBLISH_DOCKER_IMAGE_URL") ?: "docker.io"
			}
		}
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
