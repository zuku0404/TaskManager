plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "com.zuku"
version = "1.0-SNAPSHOT"

val mySqlVersion = "8.0.28"
val koinVersion = "3.1.4"
val kotestVesrion = "5.8.0"
val gsonVersion = "2.10"
val mockkVersion = "1.12.4"
val jUnitVersion = "5.10.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("mysql:mysql-connector-java:$mySqlVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    testImplementation ("io.kotest:kotest-runner-junit5-jvm:$kotestVesrion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}