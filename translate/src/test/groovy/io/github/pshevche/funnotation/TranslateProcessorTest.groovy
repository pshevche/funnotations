package io.github.pshevche.funnotation

import com.deepl.api.LanguageCode
import com.google.testing.compile.JavaFileObjects
import io.github.pshevche.funnotation.internal.TranslationService
import spock.lang.Specification

import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac

class TranslateProcessorTest extends Specification {

    TranslationService translator = Mock {
        translate(_, _) >> { args -> args[0].collect { ["translated", it] }.flatten() }
    }

    def "translates simple class"() {
        given:
        def compilation = javac()
                .withProcessors(new TranslateProcessor(translator))
                .compile(JavaFileObjects.forResource("io/github/pshevche/funnotation/input/SmokeTestClass.java"))

        expect:
        assertThat(compilation).succeededWithoutWarnings()
        assertThat(compilation).generatedSourceFile("io.github.pshevche.funnotation.TranslatedSmokeTranslatedTestTranslatedClass")
                .contentsAsUtf8String().isEqualTo("""\
package io.github.pshevche.funnotation;

public class TranslatedSmokeTranslatedTestTranslatedClass {

    private final SmokeTestClass delegate;

    public TranslatedSmokeTranslatedTestTranslatedClass(SmokeTestClass delegate) {
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

    def "respects the language setting"() {
        when:
        def compilation = javac()
                .withProcessors(new TranslateProcessor(translator))
                .compile(JavaFileObjects.forResource("io/github/pshevche/funnotation/input/SmokeTestClass.java"))

        then:
        1 * translator.translate(["smoke", "test", "class"], LanguageCode.German) >> ["translated", "smoke", "test", "class"]

        and:
        assertThat(compilation).succeededWithoutWarnings()
    }
}
