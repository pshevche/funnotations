package io.github.pshevche.funnotation.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tokenizer {
    private Tokenizer() {
    }

    public static List<String> wordsFromPascalOrCamelCase(String pascalOrCamelCaseString) {
        if (pascalOrCamelCaseString.isBlank()) {
            return Collections.emptyList();
        }

        var result = new ArrayList<String>();

        var currentWord = new StringBuilder();
        for (var i = 0; i < pascalOrCamelCaseString.length(); ++i) {
            var character = pascalOrCamelCaseString.charAt(i);
            if (Character.isUpperCase(character)) {
                if (!currentWord.isEmpty()) {
                    result.add(currentWord.toString().toLowerCase());
                }

                currentWord = new StringBuilder();
            }

            currentWord.append(character);
        }

        if (!currentWord.isEmpty()) {
            result.add(currentWord.toString().toLowerCase());
        }

        return result;
    }

    public static String toPascalCaseString(List<String> words) {
        var result = new StringBuilder();

        for (var word : words) {
            result.append(capitalized(word.toLowerCase()));
        }

        return result.toString();
    }

    public static String toCamelCaseString(List<String> words) {
        var result = new StringBuilder();

        for (var i = 0; i < words.size(); ++i) {
            var word = words.get(i).toLowerCase();
            if (i == 0) {
                result.append(word);
            } else {
                result.append(capitalized(word));
            }
        }

        return result.toString();
    }

    private static String capitalized(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
