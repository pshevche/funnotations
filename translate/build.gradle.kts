plugins {
    groovy
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(libs.auto.service)
    implementation(libs.auto.service)

    testImplementation(libs.groovy)
    testImplementation(libs.spock.core)
    testImplementation(libs.junit)
    testImplementation(libs.compile.testing)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    // https://github.com/google/compile-testing/issues/222
    jvmArgs(
        "--add-opens",
        "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens",
        "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-opens",
        "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED"
    )
}
