package io.github.pshevche.funnotation.internal;

public class DeepLApiKey {

    private static final String API_KEY_ENV_VAR = "FUNNOTATION_DEEPL_API_KEY";
    private final String value;

    private DeepLApiKey(String value) {
        this.value = value;
    }

    public static DeepLApiKey createFromSystemProperty() {
        var apiKeyValue = System.getenv(API_KEY_ENV_VAR);
        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            throw new FunnotationException(
                "DeepL API key is not provided via the 'FUNNOTATION_DEEPL_API_KEY' environment variable. " +
                "Follow the instructions at https://github.com/DeepLcom/deepl-java?tab=readme-ov-file#getting-an-authentication-key to create one"
            );
        }

        return new DeepLApiKey(apiKeyValue);
    }

    public String getValue() {
        return value;
    }
}
