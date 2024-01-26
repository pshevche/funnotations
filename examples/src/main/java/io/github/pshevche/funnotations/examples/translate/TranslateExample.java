package io.github.pshevche.funnotations.examples.translate;

import io.github.pshevche.funnotation.Translate;

/**
 * This example illustrates the usage of {@link Translate} annotation.
 */
public class TranslateExample {

    public static void main(String[] args) {
        var enterprise = new Spaceship("USS Enterprise");
        enterprise.flyFast("Mercury");

        var polishEnterprise = new StatekKosmiczny(enterprise);
        polishEnterprise.lataćSzybko("Wenus");

        var russianEnterprise = new КосмическийКорабль(enterprise);
        russianEnterprise.быстроЛетать("Земля");

        var germanEnterprise = new Raumschiff(enterprise);
        germanEnterprise.schnellFliegen("Mars");
    }

}
