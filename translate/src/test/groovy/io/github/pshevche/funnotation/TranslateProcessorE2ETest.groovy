package io.github.pshevche.funnotation

import com.google.testing.compile.JavaFileObjects
import spock.lang.Specification

import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac

class TranslateProcessorE2ETest extends Specification {

    def "translates a car class into German"() {
        given:
        def expected = JavaFileObjects.forResource("io/github/pshevche/funnotation/expected/GermanCar.java")
        def compilation = javac()
                .withProcessors(new TranslateProcessor())
                .compile(JavaFileObjects.forResource("io/github/pshevche/funnotation/input/GermanCar.java"))

        expect:
        assertThat(compilation).generatedSourceFile("io.github.pshevche.funnotation.DeutschesAuto")
                .contentsAsUtf8String().isEqualTo(expected.getCharContent(true))
    }
}
