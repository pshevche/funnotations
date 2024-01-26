package io.github.pshevche.funnotation

class TranslateProcessorE2ETest extends BaseProcessorTest<TranslateProcessor> {

    @Override
    TranslateProcessor createProcessor() {
        new TranslateProcessor()
    }

    def "translates a car class into German"() {
        given:
        def compilation = compile(input("GermanCar"))

        expect:
        assertGeneratedFileWithContent(compilation, "io.github.pshevche.funnotation.DeutschesAuto", expected("GermanCar").getCharContent(true))
    }
}
