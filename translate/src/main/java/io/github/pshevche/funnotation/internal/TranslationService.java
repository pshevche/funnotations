package io.github.pshevche.funnotation.internal;

import java.util.List;

public interface TranslationService {

    List<String> translate(List<String> words, String languageCode);
}
