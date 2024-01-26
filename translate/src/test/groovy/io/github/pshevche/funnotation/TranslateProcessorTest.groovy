package io.github.pshevche.funnotation

import com.deepl.api.LanguageCode
import io.github.pshevche.funnotation.internal.TranslationService

// TODO: handle custom params
// TODO: support various types (enums, interfaces, abstract classes etc)
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

}
""")
    }

    def "respects the language setting"() {
        when:
        compile(input("SmokeTestClass"))

        then:
        1 * translator.translate(["smoke", "test", "class"], LanguageCode.German) >> ["translated", "smoke", "test", "class"]
    }

    def "does nothing if not annotated with @Translate"() {
        when:
        def compilation = compile(input("NonTranslatableClass"))

        then:
        compilation.generatedSourceFiles().empty
        0 * translator._
    }

    def "translates only public and package-private methods"() {
        when:
        def compilation = compile(input("MethodsWithDifferentVisibility"))

        then:
        assertGeneratedFileWithContent(compilation, "io.github.pshevche.funnotation.TranslatedMethodsTranslatedWithTranslatedDifferentTranslatedVisibility", """\
package io.github.pshevche.funnotation;

public class TranslatedMethodsTranslatedWithTranslatedDifferentTranslatedVisibility {

    private final MethodsWithDifferentVisibility delegate;

    public TranslatedMethodsTranslatedWithTranslatedDifferentTranslatedVisibility(MethodsWithDifferentVisibility delegate) {
        this.delegate = delegate;
    }

    public void translatedPublicTranslatedMethod() {
        this.delegate.publicMethod();
    }

    void translatedPackageTranslatedPrivateTranslatedMethod() {
        this.delegate.packagePrivateMethod();
    }

}
""")
    }

    def "generated class has the same visibility as the original"() {
        when:
        def compilation = compile(input("PackagePrivateClass"))

        then:
        assertGeneratedFileWithContent(compilation, "io.github.pshevche.funnotation.TranslatedPackageTranslatedPrivateTranslatedClass", """\
package io.github.pshevche.funnotation;

class TranslatedPackageTranslatedPrivateTranslatedClass {

    private final PackagePrivateClass delegate;

    TranslatedPackageTranslatedPrivateTranslatedClass(PackagePrivateClass delegate) {
        this.delegate = delegate;
    }
}
""")
    }

    def "translates method parameter names"() {
        when:
        def compilation = compile(input("MethodsWithParams"))

        then:
        assertGeneratedFileWithContent(compilation, "io.github.pshevche.funnotation.TranslatedMethodsTranslatedWithTranslatedParams", """\
package io.github.pshevche.funnotation;

class TranslatedMethodsTranslatedWithTranslatedParams {

    private final MethodsWithParams delegate;

    TranslatedMethodsTranslatedWithTranslatedParams(MethodsWithParams delegate) {
        this.delegate = delegate;
    }

    public void translatedMethod1(java.lang.String translatedParam1) {
        this.delegate.method1(translatedParam1);
    }

    public void translatedMethod2(java.lang.String translatedParam2) {
        this.delegate.method2(translatedParam2);
    }

}
""")
    }

}
