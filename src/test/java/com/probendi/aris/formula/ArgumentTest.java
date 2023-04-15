package com.probendi.aris.formula;

import com.probendi.aris.exception.ArisException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgumentTest {

    @ParameterizedTest
    @ArgumentsSource(ValidateArgumentsProvider.class)
    void testIsValid(final Argument argument, final boolean expected) throws ArisException {
        assertEquals(expected, argument.isValid());
    }
}

class ValidateArgumentsProvider implements ArgumentsProvider {

    private static final KPredicate F_M = new KPredicate("F", "m");
    private static final KPredicate F_N = new KPredicate("F", "n");
    private static final KPredicate F_X = new KPredicate("F", "x");
    private static final KPredicate F_Y = new KPredicate("F", "y");
    private static final KPredicate G_M = new KPredicate("G", "m");
    private static final KPredicate G_N = new KPredicate("G", "n");
    private static final KPredicate G_X = new KPredicate("G", "x");
    private static final KPredicate H_N = new KPredicate("H", "n");
    private static final KPredicate H_O = new KPredicate("H", "o");
    private static final KPredicate H_X = new KPredicate("H", "x");
    private static final KPredicate L_O_N = new KPredicate("L", List.of("o", "n"));
    private static final KPredicate L_O_X = new KPredicate("L", List.of("o", "x"));
    private static final KPredicate L_X_N = new KPredicate("L", List.of("x", "n"));
    private static final KPredicate L_X_Y = new KPredicate("L", List.of("x", "y"));

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
        return Stream.of(
                // A - Fn, ∀x(Fx → Gx) ∴ Gn
                Arguments.of(new Argument(G_N, F_N,
                        new UniversalQuantifier("x", new Conditional(F_X, G_X))), true),
                // B - ∀x(Fx ⊃ Gx), (Fn ∧ ¬Hn) ∴ ¬∀x(Gx ⊃ Hx)
                Arguments.of(new Argument(
                        new Negation(new UniversalQuantifier("x", new Conditional(G_X, H_X))),
                        new UniversalQuantifier("x", new Conditional(F_X, G_X)),
                        new Conjunction(F_N, new Negation(H_N))), true),
                // C - ∀x(Fx ⊃ Gx), (Fm ∨ Fn) ∴ (Gm ∨ Gn)
                Arguments.of(new Argument(
                        new Disjunction(G_M, G_N),
                        new UniversalQuantifier("x", new Conditional(F_X, G_X)),
                        new Disjunction(F_M, F_N)), true),
                // D - ¬∃x(Fx ∧ Gx), Fm ∴ ¬Gm
                Arguments.of(new Argument(
                        new Negation(G_M),
                        new Negation(new ExistentialQuantifier("x", new Conjunction(F_X, G_X))),
                        F_M), true),
                // E - (Fm ∨ Fn), ∀x(Fx ⊃ Gx)∴ ∃xGx
                Arguments.of(new Argument(
                        new ExistentialQuantifier("x", G_X),
                        new Disjunction(F_M, F_N),
                        new UniversalQuantifier("x", new Conditional(F_X, G_X))), true),
                // F - ∃xFx, ∀x(Fx ⊃ Gx) ∴ ∃xGx
                Arguments.of(new Argument(
                        new ExistentialQuantifier("x", G_X),
                        new ExistentialQuantifier("x", F_X),
                        new UniversalQuantifier("x", new Conditional(F_X, G_X))), true),
                // F - ∃xFx, ∀x(Fx ⊃ Gx) ∴ ∃xGx
                Arguments.of(new Argument(
                        new ExistentialQuantifier("x", G_X),
                        new ExistentialQuantifier("x", F_X),
                        new UniversalQuantifier("x", new Conditional(F_X, G_X))), true),
                // G - ∀x(Fx ⊃ Gx), ∀x(Gx ⊃ Hx) ∴ ∀x(Fx ⊃ Hx)
                Arguments.of(new Argument(
                        new UniversalQuantifier("x", new Conditional(F_X, H_X)),
                        new UniversalQuantifier("x", new Conditional(F_X, G_X)),
                        new UniversalQuantifier("x", new Conditional(G_X, H_X))), true),
                // H - ∃xFx ∴ Fn - well-know problem (see p. 235)
                Arguments.of(new Argument(F_N, new ExistentialQuantifier("x", F_X)), false),
                // I - ∃xFx, ∃Gx ∴ ∃x(Fx ∧ Gx)
                Arguments.of(new Argument(
                        new ExistentialQuantifier("x", new Conjunction(F_X, G_X)),
                        new ExistentialQuantifier("x", F_X),
                        new ExistentialQuantifier("x", G_X)), false),
                // J - ∃x(Fx ∧ ¬Gx), ∀x(Hx ⊃ Gx) ∴ ∃x(Fx ∧ ¬Hx)
                Arguments.of(new Argument(
                        new ExistentialQuantifier("x", new Conjunction(F_X, new Negation(H_X))),
                        new ExistentialQuantifier("x", new Conjunction(F_X, new Negation(G_X))),
                        new UniversalQuantifier("x", new Conditional(H_X, G_X))), true),
                // K - (∃x(Fx ∧ ¬Gx) ⊃ ¬Gn), Gn ∴ ∀x(Fx ⊃ Gx)
                Arguments.of(new Argument(
                        new UniversalQuantifier("x", new Conditional(F_X, G_X)),
                        new Conditional(new ExistentialQuantifier("x",
                                new Conjunction(F_X, new Negation(G_X))), new Negation(G_N)),
                        new Negation(G_N)
                ), true),
                // L - (∃xLox ⊃ Ho), (∃xLxn ⊃ Lon), Lmn ∴ Ho
                Arguments.of(new Argument(
                        new KPredicate("H", "o"),
                        new Conditional(new ExistentialQuantifier("x", L_O_X), H_O),
                        new Conditional(new ExistentialQuantifier("x", L_X_N), L_O_N)), true),
                // M - ∀x∃y(Fy ∧ Lxy), ∀x(Gx ⊃ ¬Fx), Gn ∴ ¬∀xGx
                Arguments.of(new Argument(
                    new Negation(new UniversalQuantifier("x", G_X)),
                        new UniversalQuantifier("x",
                                new ExistentialQuantifier("y", new Conjunction(F_Y, L_X_Y))),
                        new UniversalQuantifier("x", new Conditional(G_X, new Negation(F_X)))), true)
        );
    }
}
