package io.github.pshevche.funnotation;

public class TranslatedSampleClass {

    private final SampleClass delegate;

    public TranslatedSampleClass(SampleClass delegate) {
        this.delegate = delegate;
    }

    public void hello() {
        this.delegate.hello();
    }

    void privilegedHello() {
        this.delegate.privilegedHello();
    }

    public void bye() {
        this.delegate.bye();
    }

    void privilegedBye() {
        this.delegate.privilegedBye();
    }

}
