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
    implementation(libs.deepl)

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
    compileTestingJvmArgs()

    filter {
        excludeTestsMatching("*.TranslateProcessorE2ETest")
    }
}

tasks.register<Test>("e2eTest") {
    useJUnitPlatform()
    systemProperty("funnotation.deepl.api.key", project.property("funnotation.deepl.api.key")!!)
    compileTestingJvmArgs()

    filter {
        includeTestsMatching("*.TranslateProcessorE2ETest")
    }
}

fun Test.compileTestingJvmArgs() {
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