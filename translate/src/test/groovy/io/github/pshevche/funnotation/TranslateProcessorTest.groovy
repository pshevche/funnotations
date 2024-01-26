package io.github.pshevche.funnotation

import com.google.testing.compile.JavaFileObjects
import io.github.pshevche.funnotation.internal.DeepLTranslator
import spock.lang.Specification

import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac

class TranslateProcessorTest extends Specification {

    DeepLTranslator translator = words -> words.collect { ["translated", it] }.flatten()

    def "translates simple class"() {
        given:
        def compilation = javac()
                .withProcessors(new TranslateProcessor(translator))
                .compile(JavaFileObjects.forResource("io/github/pshevche/funnotation/input/SampleClass.java"))

        expect:
        assertThat(compilation).succeededWithoutWarnings()
        assertThat(compilation).generatedSourceFile("io.github.pshevche.funnotation.TranslatedSampleTranslatedClass")
                .contentsAsUtf8String().isEqualTo("""\
package io.github.pshevche.funnotation;

public class TranslatedSampleTranslatedClass {

    private final SampleClass delegate;

    public TranslatedSampleTranslatedClass(SampleClass delegate) {
        this.delegate = delegate;
    }

    public void translatedHello() {
        this.delegate.hello();
    }

    void translatedPrivilegedTranslatedHello() {
        this.delegate.privilegedHello();
    }

    public void translatedBye() {
        this.delegate.bye();
    }

    void translatedPrivilegedTranslatedBye() {
        this.delegate.privilegedBye();
    }

}
""")
    }
}
