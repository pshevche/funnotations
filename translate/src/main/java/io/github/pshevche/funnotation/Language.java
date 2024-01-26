package io.github.pshevche.funnotation;

import static com.deepl.api.LanguageCode.Bulgarian;
import static com.deepl.api.LanguageCode.Chinese;
import static com.deepl.api.LanguageCode.Czech;
import static com.deepl.api.LanguageCode.Danish;
import static com.deepl.api.LanguageCode.Dutch;
import static com.deepl.api.LanguageCode.English;
import static com.deepl.api.LanguageCode.EnglishAmerican;
import static com.deepl.api.LanguageCode.EnglishBritish;
import static com.deepl.api.LanguageCode.Estonian;
import static com.deepl.api.LanguageCode.Finnish;
import static com.deepl.api.LanguageCode.French;
import static com.deepl.api.LanguageCode.German;
import static com.deepl.api.LanguageCode.Greek;
import static com.deepl.api.LanguageCode.Hungarian;
import static com.deepl.api.LanguageCode.Indonesian;
import static com.deepl.api.LanguageCode.Italian;
import static com.deepl.api.LanguageCode.Japanese;
import static com.deepl.api.LanguageCode.Korean;
import static com.deepl.api.LanguageCode.Latvian;
import static com.deepl.api.LanguageCode.Lithuanian;
import static com.deepl.api.LanguageCode.Norwegian;
import static com.deepl.api.LanguageCode.Polish;
import static com.deepl.api.LanguageCode.Portuguese;
import static com.deepl.api.LanguageCode.PortugueseBrazilian;
import static com.deepl.api.LanguageCode.PortugueseEuropean;
import static com.deepl.api.LanguageCode.Romanian;
import static com.deepl.api.LanguageCode.Russian;
import static com.deepl.api.LanguageCode.Slovak;
import static com.deepl.api.LanguageCode.Slovenian;
import static com.deepl.api.LanguageCode.Spanish;
import static com.deepl.api.LanguageCode.Swedish;
import static com.deepl.api.LanguageCode.Turkish;

/**
 * Annotation-compatible version of {@link com.deepl.api.LanguageCode}.
 */
public enum Language {
    BULGARIAN(Bulgarian),
    CZECH(Czech),
    DANISH(Danish),
    GERMAN(German),
    GREEK(Greek),
    ENGLISH(English),
    ENGLISH_BRITISH(EnglishBritish),
    ENGLISH_AMERICAN(EnglishAmerican),
    SPANISH(Spanish),
    ESTONIAN(Estonian),
    FINNISH(Finnish),
    FRENCH(French),
    HUNGARIAN(Hungarian),
    INDONESIAN(Indonesian),
    ITALIAN(Italian),
    JAPANESE(Japanese),
    KOREAN(Korean),
    LITHUANIAN(Lithuanian),
    LATVIAN(Latvian),
    NORWEGIAN(Norwegian),
    DUTCH(Dutch),
    POLISH(Polish),
    PORTUGUESE(Portuguese),
    PORTUGUESE_BRAZILIAN(PortugueseBrazilian),
    PORTUGUESE_EUROPEAN(PortugueseEuropean),
    ROMANIAN(Romanian),
    RUSSIAN(Russian),
    SLOVAK(Slovak),
    SLOVENIAN(Slovenian),
    SWEDISH(Swedish),
    TURKISH(Turkish),
    CHINESE(Chinese);

    private final String code;

    Language(String code) {
        this.code = code;
    }

    String getCode() {
        return code;
    }
}
