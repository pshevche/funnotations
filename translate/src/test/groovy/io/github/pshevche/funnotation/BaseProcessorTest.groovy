package io.github.pshevche.funnotation

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import spock.lang.Specification

import javax.annotation.processing.Processor
import javax.tools.JavaFileObject

import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac

abstract class BaseProcessorTest<T extends Processor> extends Specification {

    abstract T createProcessor()

    Compilation compile(JavaFileObject... javaFiles) {
        def compilation = javac()
                .withProcessors(createProcessor())
                .compile(javaFiles)

        assertThat(compilation).succeededWithoutWarnings()

        compilation
    }

    JavaFileObject input(String resourceName) {
        JavaFileObjects.forResource("io/github/pshevche/funnotation/input/${resourceName}.java")
    }

    JavaFileObject expected(String resourceName) {
        JavaFileObjects.forResource("io/github/pshevche/funnotation/expected/${resourceName}.java")
    }

    void assertGeneratedFileWithContent(Compilation compilation, String classFQN, CharSequence content) {
        assertThat(compilation).generatedSourceFile(classFQN).contentsAsUtf8String().isEqualTo(content)
    }

}
