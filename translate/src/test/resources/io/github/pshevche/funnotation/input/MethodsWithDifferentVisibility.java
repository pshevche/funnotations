package io.github.pshevche.funnotation;

@Translate(Language.SPANISH)
public class MethodsWithDifferentVisibility {

    public MethodsWithDifferentVisibility() {
    }

    public void publicMethod() {
    }

    void packagePrivateMethod() {
    }

    private void privateMethod() {
    }

}
