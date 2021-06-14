package com.danifoldi.bungeegui.util;

import org.jetbrains.annotations.NotNull;

public class Pair<A, B> {
    private final A a;
    private final B b;

    private Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    public static<A, B> @NotNull Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }
}
