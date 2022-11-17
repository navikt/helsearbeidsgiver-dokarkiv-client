import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val mockkVersion: String by project
val githubPassword: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jmailen.kotlinter") version "3.10.0"
    id("maven-publish")
}

group = "no.nav.helsearbeidsgiver"
version = "0.1.5"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
    maven {
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: "x-access-token"
            password = System.getenv("GITHUB_TOKEN") ?: githubPassword
        }
        setUrl("https://maven.pkg.github.com/navikt/*")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/helsearbeidsgiver-${rootProject.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("com.nimbusds:nimbus-jose-jwt:9.22")
    implementation("no.nav.helsearbeidsgiver:tokenprovider:0.1.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")
    testImplementation(kotlin("test"))
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
}
