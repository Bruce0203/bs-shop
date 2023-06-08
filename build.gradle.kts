import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.distsDirectory

val ktorVersion: String by project
val kotlinVersion: String by project
val exposedVersion: String by project
val kotlinCssVersion: String by project
val fritz2Version: String by project
plugins {
    kotlin("multiplatform") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
}

group = "io.github.bruce0203.baekshinshop"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            binaries.executable()
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            repositories {
                mavenCentral()
            }
            dependencies {
                implementation("dev.fritz2:core:$fritz2Version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-css:$kotlinCssVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.1")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-server-cors:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:+")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
                implementation("com.h2database:h2:2.1.214")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
                implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.467")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.467")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.10.5-pre.467")
            }
        }
        val jsTest by getting
    }
}


application {
    mainClass.set("org.example.application.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<Copy>("jsProcessResources") {
    filesMatching("**/index.html") {
        expand("project" to project.name)
    }
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    ksp("dev.fritz2:lenses-annotation-processor:$fritz2Version")
}

dependencies {
    add("kspCommonMainMetadata", "dev.fritz2:lenses-annotation-processor:$fritz2Version")
}
tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if (name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
}

tasks.shadowJar {
    archiveFileName.set("${project.name}.jar")
    archiveVersion.set("")
}