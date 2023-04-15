package com.probendi.aris;

import com.probendi.aris.exception.ArisException;
import com.probendi.aris.exception.ParserException;
import com.probendi.aris.formula.*;
import com.probendi.aris.token.Token;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @ParameterizedTest
    @ArgumentsSource(ParseArgumentsProvider.class)
    void testParseArgument(final List<Token> line, final Map<String, Argument> expected) throws ArisException {
        final Queue<Queue<Token>> queue = new LinkedList<>();
        queue.add(new LinkedList<>(line));
        final Parser parser = new Parser();
        parser.parse(queue);
        final Map<String, Argument> actual = parser.getArguments();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ArgumentsSource(ParseFailsArgumentsProvider.class)
    void testParseArgumentFails(final List<Token> line, final String message) {
        final Queue<Queue<Token>> queue = new LinkedList<>();
        queue.add(new LinkedList<>(line));
        final ParserException e = assertThrows(ParserException.class, () -> new Parser().parse(queue));
        assertEquals(message, e.getMessage());

    }

    static class ParseArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
            return Stream.of(
                    // argument a := Fn, ∀x(Fx → Gx) ∴ Gn
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, PRED_F, N, COMMA, EVERY, X, L_BRACKET,
                                    PRED_F, X, IMPLICATION, PRED_G, X, R_BRACKET, THEREFORE, PRED_G, N)),
                            Map.of("a",
                                    new Argument(new KPredicate("G", "n"),
                                            new KPredicate("F", "n"),
                                            new UniversalQuantifier("x", new Conditional(
                                                    new KPredicate("F", "x"),
                                                    new KPredicate("G", "x")
                                            )
                                            ))
                            )),
                    // argument a := ∃x(Px ∧ Wx), ∀x(Kx → ¬Wx) ∴ ∃x(Px ∧ ¬Kx)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, L_BRACKET, PRED_P, X, AND, PRED_W,
                                    X, R_BRACKET, COMMA, EVERY, X, L_BRACKET, PRED_K, X, IMPLICATION, NOT, PRED_W, X,
                                    R_BRACKET, THEREFORE, EXIST, X, L_BRACKET, PRED_P, X, AND, NOT, PRED_K, X, R_BRACKET)),
                            Map.of("a",
                                    new Argument(new ExistentialQuantifier("x", new Conjunction(
                                            new KPredicate("P", "x"),
                                            new Negation(new KPredicate("K", "x")))
                                    ), new ExistentialQuantifier("x", new Conjunction(
                                            new KPredicate("P", "x"), new KPredicate("W", "x")
                                    )), new UniversalQuantifier("x", new Conditional(
                                            new KPredicate("K", "x"),
                                            new Negation(new KPredicate("W", "x"))
                                    ))
                                    ))
                    )
            );
        }
    }

    static class ParseFailsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    // ∃x∃z∃x(Fm ∧ Lmn)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, EXIST, Z, EXIST, X,
                                    L_BRACKET, PRED_F, M, AND, PRED_L, M, N, R_BRACKET)),
                            "The variable 'x' is reused in formula '∃x∃z∃x(Fm ∧ Lmn)'"
                    ),
                    // ∃x∃z∃y∃z(Fm ∧ Lmn)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, EXIST, Z, EXIST, Y,
                                    EXIST, Z, L_BRACKET, PRED_F, M, AND, PRED_L, M, N, R_BRACKET)),
                            "The variable 'z' is reused in formula '∃x∃z∃y∃z(Fm ∧ Lmn)'"
                    ),
                    // ∀x(Fx → Hn)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EVERY, X, L_BRACKET, PRED_F, X,
                                    IMPLICATION, PRED_H, N, R_BRACKET)),
                            "Variables mismatch in '∀x(Fx → Hn)'"
                    ),
                    // ∀x(Fx → Hz)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EVERY, X, L_BRACKET, PRED_F, X,
                                    IMPLICATION, PRED_H, Z, R_BRACKET)),
                            "Variables mismatch in '∀x(Fx → Hz)'"
                    ),
                    // ∃x∃z(Fm ∧ Lmn)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, EXIST, Z, L_BRACKET,
                                    PRED_F, M, AND, PRED_L, M, N, R_BRACKET)),
                            "Variables mismatch in '∃x∃z(Fm ∧ Lmn)'"
                    ),
                    // ∃x∃z(Fz ∧ Lzn)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, EXIST, Z, L_BRACKET,
                                    PRED_F, Z, AND, PRED_L, Z, N, R_BRACKET)),
                            "Variables mismatch in '∃x∃z(Fz ∧ Lzn)'"
                    ),
                    // ∃x∃z(Fn ∧ Lnx)
                    Arguments.of(
                            new LinkedList<>(List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, EXIST, Z, L_BRACKET,
                                    PRED_F, N, AND, PRED_L, N, X, R_BRACKET)),
                            "Variables mismatch in '∃x∃z(Fn ∧ Lnx)'"
                    )
            );
        }
    }

}

