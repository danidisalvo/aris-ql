package com.probendi.aris.token;

import java.util.List;

/**
 * A variable.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Variable extends Token {

    /**
     * Creates a new {@code variable} token with the given value.
     *
     * @param value the value
     */
    public Variable(final String value) {
        super(List.of(
                And.class,
                Assign.class,
                Comma.class,
                Exist.class,
                Every.class,
                MaterialImplication.class,
                LBracket.class,
                Or.class,
                Variable.class,
                RBracket.class,
                Therefore.class), value
        );
    }

    /**
     * Creates a new {@code variable} token with the given value.
     *
     * @param c the value
     */
    public Variable(final char c) {
        this(String.valueOf(c));
    }
}
