package io.github.pshevche.funnotation

import com.deepl.api.LanguageCode
import io.github.pshevche.funnotation.internal.TranslationService

class TranslateProcessorTest extends BaseProcessorTest<TranslateProcessor> {

    TranslationService translator = Mock {
        translate(_, _) >> { args -> args[0].collect { ["translated", it] }.flatten() }
    }

    @Override
    TranslateProcessor createProcessor() {
        new TranslateProcessor(translator)
    }

    def "translates simple class"() {
        given:
        def compilation = compile(input("SmokeTestClass"))

        expect:
        assertGeneratedFileWithContent(compilation, "io.github.pshevche.funnotation.TranslatedSmokeTranslatedTestTranslatedClass", """\
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
        def compilation = compile(input("SmokeTestClass"))

        then:
        1 * translator.translate(["smoke", "test", "class"], LanguageCode.German) >> ["translated", "smoke", "test", "class"]
    }

}
