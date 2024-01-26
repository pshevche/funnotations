package io.github.pshevche.funnotation;

public class Auto {

    private final Car delegate;

    public Auto(Car delegate) {
        this.delegate = delegate;
    }

    public void fahrt() {
        this.delegate.ride();
    }

}
