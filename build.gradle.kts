plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "com.zuku"
version = "1.0-SNAPSHOT"
val mySqlVersion = "8.0.28"
val koinVersion = "3.1.4"
val kotestVesrion = "5.8.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("mysql:mysql-connector-java:$mySqlVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    testImplementation ("io.kotest:kotest-runner-junit5-jvm:$kotestVesrion")
    implementation("com.google.code.gson:gson:2.10")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("io.mockk:mockk:1.12.4")
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