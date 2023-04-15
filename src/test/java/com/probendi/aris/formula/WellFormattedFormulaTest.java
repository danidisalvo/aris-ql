package com.probendi.aris.formula;

import com.probendi.aris.exception.ArisException;
import com.probendi.aris.exception.MissingSymbolException;
import com.probendi.aris.token.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

import static com.probendi.aris.LexicalAnalyzerTest.*;
import static org.junit.jupiter.api.Assertions.*;

public class WellFormattedFormulaTest {

    private static final KPredicate PRED_P = new KPredicate("P", "n");
    private static final KPredicate PRED_Q = new KPredicate("Q", "n");
    static final AtomicCondition P = new AtomicCondition(PRED_P);
    static final AtomicCondition Q = new AtomicCondition(PRED_Q);
    private static final Negation NOT_P = new Negation(P);
    private static final Negation NOT_Q = new Negation(Q);
    private static final Disjunction DIS_NOT_P_NOT_Q = new Disjunction(NOT_P, NOT_Q);

    static final Condition P_FALSE = new AtomicCondition(PRED_P).setFalse();
    static final Condition P_TRUE = new AtomicCondition(PRED_P).setTrue();
    static final Condition Q_FALSE = new AtomicCondition(PRED_Q).setFalse();
    static final Condition Q_TRUE = new AtomicCondition(PRED_Q).setTrue();

    @ParameterizedTest
    @ArgumentsSource(ParseArgumentsProvider.class)
    void testParse(final Queue<Token> tokens, final WellFormedFormula expected) throws ArisException {
        final WellFormedFormula actual = WellFormedFormula.parse(tokens);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ArgumentsSource(DetermineTruthConditionsArgumentsProvider.class)
    void testDetermineTruthConditions(final WellFormedFormula wff, final List<Condition> expected) {
        final List<Condition> conditions = wff.determineTruthnessConditions();
        System.out.println(conditions);
        assertEquals(expected, conditions);
    }

    @ParameterizedTest
    @ArgumentsSource(ValuationArgumentsProvider.class)
    void testValuation(final WellFormedFormula wff, final Map<KPredicate, Boolean> values, final boolean expected) throws ArisException {
        assertEquals(expected, wff.valuate(values));
    }

    @Test
    void testValuationFailsMissingSymbolException() {
        final Map<KPredicate, Boolean> values = Map.of(PRED_Q, true);
        final MissingSymbolException e = assertThrows(MissingSymbolException.class,
                () -> ((WellFormedFormula) NOT_P).valuate(values));
        assertEquals("P[n]", e.getMessage());
    }

    static class ParseArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    // ¬Fn
                    Arguments.of(
                            new LinkedList<>(List.of(NOT, PRED_F, N)),
                            new Negation(new KPredicate("F","n"))
                    ),
                    // (Fn ∧ Fo)
                    Arguments.of(
                            new LinkedList<>(List.of(L_BRACKET, PRED_F, N, AND, PRED_F, O, R_BRACKET)),
                            new Conjunction(new KPredicate("F", "n"), new KPredicate("F", "o"))
                    ),
                    // (Rmno → (Lom ∧ ¬Lon))
                    Arguments.of(
                            new LinkedList<>(List.of(L_BRACKET, PRED_R, M, N, O, IMPLICATION, L_BRACKET, PRED_L, O, M,
                                    AND, NOT, PRED_L, O, N, R_BRACKET, R_BRACKET)),
                            new Conditional(
                                    new KPredicate("R", List.of("m", "n", "o")),
                                    new Conjunction(
                                            new KPredicate("L", List.of("o", "m")),
                                            new Negation(new KPredicate("L", List.of("o", "n")))
                                    )

                            )
                    ),
                    // ∀x(Fx → Hx)
                    Arguments.of(
                            new LinkedList<>(List.of(EVERY, X, L_BRACKET, PRED_F, X, IMPLICATION, PRED_H, X, R_BRACKET)),
                            new UniversalQuantifier("x", new Conditional(
                                    new KPredicate("F", "x"), new KPredicate("H", "x")
                            ))
                    ),
                    // ∀x''(Fx'' → Hx'')
                    Arguments.of(
                            new LinkedList<>(List.of(EVERY, X_P_P, L_BRACKET, PRED_F, X_P_P, IMPLICATION, PRED_H, X_P_P, R_BRACKET)),
                            new UniversalQuantifier("x''", new Conditional(
                                    new KPredicate("F", "x''"), new KPredicate("H", "x''")
                            ))
                    ),
                    // ∃x∃z(Fz ∧ Lzx)
                    Arguments.of(
                            new LinkedList<>(List.of(EXIST, X, EXIST, Z, L_BRACKET, PRED_F, Z, AND, PRED_L, Z, X, R_BRACKET)),
                            new ExistentialQuantifier("x", new ExistentialQuantifier("z",
                                    new Conjunction(
                                            new KPredicate("F", List.of("z")),
                                            new KPredicate("L", List.of("z", "x"))
                                    )
                            ))
                    )
            );
        }
    }

    static class ValuationArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                    Arguments.of(NOT_P, Map.of(PRED_P, true), false),
                    Arguments.of(NOT_P, Map.of(PRED_P, false), true),
                    Arguments.of(DIS_NOT_P_NOT_Q, Map.of(PRED_P, true, PRED_Q, false), true),
                    Arguments.of(DIS_NOT_P_NOT_Q, Map.of(PRED_P, true, PRED_Q, true), false)
           );
        }
    }

    static class DetermineTruthConditionsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                    Arguments.of(P, List.of(P_TRUE)),
                    Arguments.of(NOT_P, List.of(P_FALSE)),
                    Arguments.of(new Negation(NOT_P), List.of(P_TRUE)),
                    Arguments.of(new Negation(new Negation(NOT_P)), List.of(P_FALSE)),
                    Arguments.of(new Conjunction(P, Q), List.of(new BinaryCondition(P_TRUE, Q_TRUE))),
                    Arguments.of(new Conjunction(P, NOT_Q), List.of(new BinaryCondition(P_TRUE, Q_FALSE))),
                    Arguments.of(new Conjunction(NOT_P, NOT_Q), List.of(new BinaryCondition(P_FALSE, Q_FALSE))),
                    Arguments.of(new Negation(new Conjunction(P, Q)), List.of(
                            new BinaryCondition(P_FALSE, Q_FALSE),
                            new BinaryCondition(P_FALSE, Q_TRUE),
                            new BinaryCondition(P_TRUE, Q_FALSE)
                    )),
                    Arguments.of(new Negation(new Conjunction(NOT_P, NOT_Q)), List.of(
                            new BinaryCondition(P_TRUE, Q_TRUE),
                            new BinaryCondition(P_TRUE, Q_FALSE),
                            new BinaryCondition(P_FALSE, Q_TRUE)
                    )),
                    Arguments.of(new Disjunction(P, Q), List.of(
                            new BinaryCondition(P_TRUE, Q_FALSE),
                            new BinaryCondition(P_FALSE, Q_TRUE),
                            new BinaryCondition(P_TRUE, Q_TRUE)
                    )),
                    Arguments.of(new Negation(new Disjunction(P, Q)), List.of(new BinaryCondition(P_FALSE, Q_FALSE))),
                    Arguments.of(new Conditional(P, Q), List.of(
                            new BinaryCondition(P_FALSE, Q_FALSE),
                            new BinaryCondition(P_FALSE, Q_TRUE),
                            new BinaryCondition(P_TRUE, Q_TRUE)
                    ))
            );
        }
    }
}
