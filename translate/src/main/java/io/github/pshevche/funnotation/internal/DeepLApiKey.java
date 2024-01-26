package io.github.pshevche.funnotation.internal;

public class DeepLApiKey {

    private static final String API_KEY_SYS_PROP = "funnotation.deepl.api.key";
    private final String value;

    private DeepLApiKey(String value) {
        this.value = value;
    }

    public static DeepLApiKey createFromSystemProperty() {
        var apiKeyValue = System.getProperty(API_KEY_SYS_PROP);
        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            throw new FunnotationException("DeepL API key is not provided");
        }

        return new DeepLApiKey(apiKeyValue);
    }

    public String getValue() {
        return value;
    }
}
