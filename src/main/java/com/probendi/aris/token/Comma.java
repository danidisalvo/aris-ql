package com.probendi.aris.token;

import java.util.List;

/**
 * The {@code ,} token.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Comma extends Token {

    /**
     * Creates a new {@code ,} token.
     */
    public Comma() {
        super(List.of(Every.class, Exist.class, LBracket.class, Not.class, Predicate.class));
    }

    @Override
    public String toString() {
        return ",";
    }
}
