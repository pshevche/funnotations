plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(project(":translate"))
    implementation(project(":translate"))
}

tasks.register<JavaExec>("translate") {
    group = "funnotations"
    description = "Run the @Translate example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.pshevche.funnotations.examples.translate.TranslateExample"
}
