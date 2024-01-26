package io.github.pshevche.funnotations.examples.translate;

import io.github.pshevche.funnotation.Language;
import io.github.pshevche.funnotation.Translate;

@Translate({Language.POLISH})
public class Spaceship {

    private final String name;

    public Spaceship(String name) {
        this.name = name;
    }

    public void flyFast(String destination) {
        System.out.printf("%s flies fast to %s%n", name, destination);
    }
}
