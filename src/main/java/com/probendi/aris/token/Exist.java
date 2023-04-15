package com.probendi.aris.token;

import java.util.List;

/**
 * The {@code ∃} token.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Exist extends Token {

    /**
     * Creates a new {@code ∃} token.
     */
    public Exist() {
        super(List.of(Variable.class));
    }

    @Override
    public String toString() {
        return "∃";
    }
}
