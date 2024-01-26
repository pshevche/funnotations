package io.github.pshevche.funnotation.internal

import spock.lang.Specification

import static io.github.pshevche.funnotation.internal.Tokenizer.*

class TokenizerTest extends Specification {

    def "handles words in PascalCase"() {
        expect:
        wordsFromPascalOrCamelCase(input) == expected
        toPascalCaseString(expected) == input

        where:
        input       | expected
        ""          | []
        "Word"      | ["word"]
        "TwoWords"  | ["two", "words"]
        "Two_Words" | ["two_", "words"]
    }

    def "handles words in camelCase"() {
        expect:
        wordsFromPascalOrCamelCase(input) == expected
        toCamelCaseString(expected) == input

        where:
        input       | expected
        ""          | []
        "word"      | ["word"]
        "twoWords"  | ["two", "words"]
        "two_Words" | ["two_", "words"]
    }
}
