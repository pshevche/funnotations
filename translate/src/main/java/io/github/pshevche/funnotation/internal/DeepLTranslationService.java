package io.github.pshevche.funnotation.internal;

import com.deepl.api.DeepLException;
import com.deepl.api.Translator;
import com.deepl.api.TranslatorOptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DeepLTranslationService implements TranslationService {

    private static final TranslatorOptions DEFAULT_OPTIONS = new TranslatorOptions()
        .setMaxRetries(3)
        .setTimeout(Duration.ofSeconds(5));

    private final Translator translator;

    public DeepLTranslationService(DeepLApiKey apiKey) {
        this.translator = new Translator(apiKey.getValue(), DEFAULT_OPTIONS);
    }

    @Override
    public List<String> translate(List<String> words, String languageCode) {
        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        var inputAsText = String.join(" ", words);
        try {
            var translationResult = translator.translateText(inputAsText, null, languageCode);
            return Arrays.asList(sanitized(translationResult.getText()).split(" "));
        } catch (DeepLException e) {
            throw new FunnotationException("Could not translate the words " + inputAsText, e);
        } catch (InterruptedException e) {
            throw new FunnotationException("Translating the words " + inputAsText + " timed out", e);
        }
    }

    // TODO: needs test
    private String sanitized(String text) {
        return text.replaceAll("\\p{Punct}", "");
    }

}
