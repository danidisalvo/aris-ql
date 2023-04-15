package com.probendi.aris;

import com.probendi.aris.exception.ArisException;
import com.probendi.aris.exception.UnexpectedCharacterException;
import com.probendi.aris.exception.UnexpectedEndOfLineException;
import com.probendi.aris.exception.UnexpectedSymbolException;
import com.probendi.aris.token.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexicalAnalyzerTest {

    public static final And AND = new And();
    public static final Argument ARGUMENT = new Argument();
    public static final Assign ASSIGN = new Assign();
    public static final Comma COMMA = new Comma();
    public static final Every EVERY = new Every();
    public static final Exist EXIST = new Exist();
    public static final Identifier ARG_A = new Identifier("a");
    public static final LBracket L_BRACKET = new LBracket();
    public static final MaterialImplication IMPLICATION = new MaterialImplication();
    public static final Not NOT = new Not();
    public static final Predicate PRED_F = new Predicate("F");
    public static final Predicate PRED_G = new Predicate("G");
    public static final Predicate PRED_H = new Predicate("H");
    public static final Predicate PRED_K = new Predicate("K");
    public static final Predicate PRED_L = new Predicate("L");
    public static final Predicate PRED_P = new Predicate("P");
    public static final Predicate PRED_R = new Predicate("R");
    public static final Predicate PRED_W = new Predicate("W");
    public static final Print PRINT = new Print();
    public static final ProperName M = new ProperName("m");
    public static final ProperName N = new ProperName("n");
    public static final ProperName O = new ProperName("o");
    public static final RBracket R_BRACKET = new RBracket();
    public static final Therefore THEREFORE = new Therefore();
    public static final Validate VALIDATE = new Validate();
    public static final Variable X = new Variable("x");
    public static final Variable X_P = new Variable("x'");
    public static final Variable X_P_P = new Variable("x''");
    public static final Variable Y = new Variable("y");
    public static final Variable Z = new Variable("z");

    @ParameterizedTest
    @ArgumentsSource(LexicalAnalyzerArgumentsProvider.class)
    void testTokenize(final String line, final List<List<Token>> expected) throws IOException, ArisException {
        final LexicalAnalyzer lexer;
        try (final Reader reader = new StringReader(line)) {
            lexer = new LexicalAnalyzer(reader);
            lexer.tokenize();
        }
        assertEquals(expected, lexer.getTokens());
    }

    @ParameterizedTest
    @ArgumentsSource(LexicalAnalyzerFailsArgumentsProvider.class)
    void testTokenizeFails(final String line, final Class<? extends ArisException> clazz, final String message) throws IOException {
        try (final Reader reader = new StringReader(line)) {
            final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(reader);
            final ArisException e = assertThrows(ArisException.class, lexicalAnalyzer::tokenize);
            assertEquals(clazz.getSimpleName(), e.getClass().getSimpleName());
            assertEquals(message, e.getMessage());
        }
    }

    static class LexicalAnalyzerArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                    Arguments.of("print", List.of(
                            List.of(PRINT))),
                    Arguments.of("print \"Hello, Aris!\"", List.of(
                            List.of(PRINT, new TString("Hello, Aris!")))),
                    Arguments.of("argument a := ¬Fn", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, NOT, PRED_F, N))),
                    Arguments.of("validate a", List.of(
                            List.of(VALIDATE, ARG_A))),
                    Arguments.of("argument a := (Fn ∧ Fo)", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, L_BRACKET, PRED_F, N, AND, PRED_F, O, R_BRACKET))),
                    Arguments.of("argument a := (Go → Ho)", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, L_BRACKET, PRED_G, O, IMPLICATION, PRED_H, O, R_BRACKET))),
                    Arguments.of("argument a := (Rmno → (Lom ∧ ¬Lon))", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, L_BRACKET, PRED_R, M, N, O, IMPLICATION,
                                    L_BRACKET, PRED_L, O, M, AND, NOT, PRED_L, O, N, R_BRACKET, R_BRACKET))),
                    Arguments.of("argument a := ∀x(Fx → Hx)", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, EVERY, X, L_BRACKET, PRED_F, X, IMPLICATION, PRED_H, X, R_BRACKET))),
                    Arguments.of("argument a := ∀x'(Fx' → Hx')", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, EVERY, X_P, L_BRACKET, PRED_F, X_P, IMPLICATION, PRED_H, X_P, R_BRACKET))),
                    Arguments.of("argument a := ∀x''(Fx'' → Hx'')", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, EVERY, X_P_P, L_BRACKET, PRED_F, X_P_P, IMPLICATION, PRED_H, X_P_P, R_BRACKET))),
                    Arguments.of("argument a := ∃x∃z(Fz ∧ Lzx)", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, EXIST, Z, L_BRACKET, PRED_F, Z, AND, PRED_L, Z, X, R_BRACKET))),
                    Arguments.of("argument a := ∃x(Px ∧ Wx), ∀x(Kx → ¬Wx) ∴ ∃x(Px ∧ ¬Kx)", List.of(
                            List.of(ARGUMENT, ARG_A, ASSIGN, EXIST, X, L_BRACKET, PRED_P, X, AND, PRED_W, X, R_BRACKET, COMMA,
                                    EVERY, X, L_BRACKET, PRED_K, X, IMPLICATION, NOT, PRED_W, X, R_BRACKET, THEREFORE,
                                    EXIST, X, L_BRACKET, PRED_P, X, AND, NOT, PRED_K, X, R_BRACKET)))
            );
        }
    }

    static class LexicalAnalyzerFailsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                    Arguments.of("P* := true", UnexpectedCharacterException.class,
                            "Unexpected character '*' at position 1 of line 'P* := true'"),
                    Arguments.of("argument arg' !P therefore Q", UnexpectedCharacterException.class,
                            "Unexpected character ''' at position 12 of line 'argument arg' !P therefore Q'"),
                    Arguments.of("(P >Q)", UnexpectedCharacterException.class,
                            "Unexpected character '>' at position 3 of line '(P >Q)'"),
                    Arguments.of("P thereforeQ", UnexpectedCharacterException.class,
                            "Unexpected character 'Q' at position 11 of line 'P thereforeQ'"),
                    Arguments.of("P is \"All men are mortal", UnexpectedCharacterException.class,
                            "Unexpected character 'l' at position 23 of line 'P is \"All men are mortal'"),
                    Arguments.of("P :- true", UnexpectedCharacterException.class,
                            "Unexpected character '-' at position 3 of line 'P :- true'"),
                    Arguments.of("P :", UnexpectedCharacterException.class,
                            "Unexpected character ':' at position 2 of line 'P :'"),
                    Arguments.of("P Q", UnexpectedSymbolException.class,
                            "Unexpected symbol 'P' at position 1 of line 'P Q'"),
                    Arguments.of("(P & Q)", UnexpectedSymbolException.class,
                            "Unexpected symbol '(' at position 1 of line '(P & Q)'"),
                    Arguments.of("!P therefore Q therefore R", UnexpectedSymbolException.class,
                            "Unexpected symbol 'therefore' at position 15 of line '!P therefore Q therefore R'"),
                    Arguments.of("P", UnexpectedEndOfLineException.class,
                            "Unexpected end of line at line 'P'"),
                    // the following error is due to the fact that print can only be followed by a string
                    Arguments.of("print H", UnexpectedSymbolException.class,
                            "Unexpected symbol 'H' at position 1 of line 'print H'"),
                    // the following error is due to the fact that print can only be followed by a string
                    Arguments.of("print Fm", UnexpectedSymbolException.class,
                            "Unexpected symbol 'F' at position 1 of line 'print Fm'"),
                    // the following error is due to the fact that e is not a valid name
                    Arguments.of("print Hello, Aris!", UnexpectedCharacterException.class,
                            "Unexpected character 'e' at position 7 of line 'print Hello, Aris!'"),
                    Arguments.of("argument a := F(n ∧ o)", UnexpectedCharacterException.class,
                            "Unexpected character '(' at position 15 of line 'argument a := F(n ∧ o)'"),
                    Arguments.of("Pmh Q", UnexpectedCharacterException.class,
                            "Unexpected character 'h' at position 2 of line 'Pmh Q'"),
                    Arguments.of("validate Q", UnexpectedSymbolException.class,
                            "Unexpected symbol 'Q' at position 1 of line 'validate Q'"),
                    Arguments.of("argument a := ∃m(Fm ∧ Lm)", UnexpectedCharacterException.class,
                            "Unexpected character 'm' at position 15 of line 'argument a := ∃m(Fm ∧ Lm)'")
            );
        }
    }
}
