package com.probendi.aris.token;

import java.util.List;

/**
 * An expression which can combine with one or more singular terms (i.e., proper names, dummy names,
 * and functional terms) or other expressions to form a sentence.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Predicate extends Token {

    /**
     * Creates a new {@code predicate} token with the given symbol.
     *
     * @param value the predicate's symbol
     */
    public Predicate(final String value) {
        super(List.of(ProperName.class, Variable.class), value);
    }

    /**
     * Creates a new {@code predicate} token with the given symbol.
     *
     * @param c the predicate's symbol
     */
    public Predicate(final char c) {
        this(String.valueOf(c));
    }
}
