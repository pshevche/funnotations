package io.github.pshevche.funnotations.examples.translate;

import io.github.pshevche.funnotation.Translate;

/**
 * This example illustrates the usage of {@link Translate} annotation.
 */
public class TranslateExample {

    public static void main(String[] args) {
        var enterprise = new Spaceship("USS Enterprise");
        enterprise.flyFast("Mars");

        var polishEnterprise = new StatekKosmiczny(enterprise);
        polishEnterprise.lataćSzybko("Earth");
    }

}
