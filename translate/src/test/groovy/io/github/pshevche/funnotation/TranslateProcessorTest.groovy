package io.github.pshevche.funnotation

import com.google.testing.compile.JavaFileObjects
import spock.lang.Specification

import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac

class TranslateProcessorTest extends Specification {

    def "smoke test"() {
        given:
        def compilation = javac()
                .withProcessors(new TranslateProcessor())
                .compile(JavaFileObjects.forResource("io/github/pshevche/funnotation/EmptyClass.java"))

        expect:
        assertThat(compilation).succeededWithoutWarnings()
    }
}
