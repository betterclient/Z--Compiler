package io.github.betterclient.compiler.api.util;

/**
 * Either A or B
 * @param <A> one of em
 * @param <B> second of em
 */
public class Either<A, B> {
    private final A aInstance;
    private final B bInstance;

    public Either(A a, boolean aa) {
        if(a == null) throw new NullPointerException("nuh uh");

        this.aInstance = a;
        this.bInstance = null;
    }

    public Either(B b) {
        if(b == null) throw new NullPointerException("nuh uh");

        this.aInstance = null;
        this.bInstance = b;
    }

    public A getAInstance() {
        return aInstance;
    }

    public B getBInstance() {
        return bInstance;
    }
}
