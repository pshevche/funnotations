package io.github.pshevche.funnotation;

public class DeutschesAuto {

    private final GermanCar delegate;

    public DeutschesAuto(GermanCar delegate) {
        this.delegate = delegate;
    }

    public void fahrt() {
        this.delegate.ride();
    }

}
