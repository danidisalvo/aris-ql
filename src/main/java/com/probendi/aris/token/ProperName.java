package com.probendi.aris.token;

import java.util.List;

/**
 * A proper name.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class ProperName extends Token {

    /**
     * Creates a new {@code proper name} token with the given value.
     *
     * @param value the value
     */
    public ProperName(final String value) {
        super(List.of(
                And.class,
                Assign.class,
                Comma.class,
                MaterialImplication.class,
                LBracket.class,
                Or.class,
                ProperName.class,
                RBracket.class,
                Therefore.class), value
        );
    }

    /**
     * Creates a new {@code proper name} token with the given value.
     *
     * @param c the value
     */
    public ProperName(final char c) {
        this(String.valueOf(c));
    }
}
