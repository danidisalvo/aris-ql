package com.probendi.aris.token;

import java.util.List;

/**
 * The {@code ¬} token.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Not extends Token {

    /**
     * Creates a new {@code ¬} token.
     */
    public Not() {
        super(List.of(LBracket.class, Not.class, Predicate.class));
    }

    @Override
    public String toString() {
        return "¬";
    }
}
