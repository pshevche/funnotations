package io.github.pshevche.funnotation

import com.google.testing.compile.JavaFileObjects
import spock.lang.Specification

import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac

class TranslateProcessorTest extends Specification {

    def "translates simple class"() {
        given:
        def compilation = javac()
                .withProcessors(new TranslateProcessor())
                .compile(JavaFileObjects.forResource("io/github/pshevche/funnotation/input/SampleClass.java"))

        expect:
        assertThat(compilation).succeededWithoutWarnings()
        assertThat(compilation).generatedSourceFile("io.github.pshevche.funnotation.TranslatedSampleClass")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("io/github/pshevche/funnotation/expected/SampleClass.java"))
        assertThat(compilation).generatedSourceFile("io.github.pshevche.funnotation.TranslatedSampleClass")
                .contentsAsUtf8String().isEqualTo("""\
package io.github.pshevche.funnotation;

public class TranslatedSampleClass {

    private final SampleClass delegate;

    public TranslatedSampleClass(SampleClass delegate) {
        this.delegate = delegate;
    }

    public void hello() {
        this.delegate.hello();
    }

    void privilegedHello() {
        this.delegate.privilegedHello();
    }

    public void bye() {
        this.delegate.bye();
    }

    void privilegedBye() {
        this.delegate.privilegedBye();
    }

}
""")
    }

    // TODO pshevche: interface, enum, class only with util methods, inheritance
}
