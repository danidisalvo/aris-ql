package com.probendi.aris;

import com.probendi.aris.exception.ArisException;
import com.probendi.aris.exception.ParserException;
import com.probendi.aris.exception.UnexpectedSymbolException;
import com.probendi.aris.formula.Argument;
import com.probendi.aris.formula.KPredicate;
import com.probendi.aris.formula.Quantifier;
import com.probendi.aris.formula.WellFormedFormula;
import com.probendi.aris.token.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * {@code Parser} parses and interprets a list of tokens.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Parser {

    private final Map<String, Argument> arguments = new HashMap<>();
    private final Map<String, Boolean> assertions = new HashMap<>();
    private final Map<KPredicate, Boolean> values = new HashMap<>();
    private final Map<String, Boolean> valuations = new HashMap<>();
    private final Map<String, Boolean> validations = new HashMap<>();

    private static final String ARGUMENT_INVALID = "argument \"%s\" is invalid\n";
    private static final String ARGUMENT_IS_NOT_TAUTOLOGY = "argument \"%s\" is not a tautology\n";
    private static final String ARGUMENT_IS_TAUTOLOGY = "argument \"%s\" is a tautology\n";
    private static final String ARGUMENT_VALID = "argument \"%s\" is valid\n";
    private static final String VALUATION = "argument \"%s\" is %b\n";

    /**
     * Parses and interprets the given queue.
     *
     * @param queue the tokens to be parsed
     * @throws ArisException if the program cannot be parsed and interpreted
     */
    public void parse(final Queue<Queue<Token>> queue) throws ArisException {
        if (queue == null) {
            throw new IllegalArgumentException("queue cannot be null");
        }
        // we parse and interpret all lines one by one.
        // we assume that the lexer already processed the input file
        // consequently, we invoke the remove method rather than the poll method
        // therefore, there need not confirm that the poll method did not return null
        String line = "";
        try {
            while (queue.peek() != null) {
                final Queue<Token> tokens = queue.remove();
                line = tokens.toString();
                final Token token = tokens.remove();
                if (token == null) continue; // this should never happen

                if (token instanceof Print) {
                    if (tokens.peek() != null) {
                        System.out.println(tokens.remove());
                    } else {
                        System.out.println();
                    }
                    continue;
                }

                final Token nextToken = tokens.remove();
                if (token instanceof com.probendi.aris.token.Argument) {
                    final Identifier identifier = (Identifier) nextToken;
                    tokens.remove();
                    final Argument argument = new Argument();
                    while (!tokens.isEmpty()) {
                        final WellFormedFormula formula = WellFormedFormula.parse(tokens);
                        if (formula == null) {
                            final Token t = tokens.remove();
                            if (!(t instanceof RBracket) && argument.getConclusion() != null) {
                                throw new UnexpectedSymbolException();
                            }
                            if (t instanceof Therefore) {
                                argument.setConclusion(WellFormedFormula.parse(tokens));
                            }
                        } else if (formula instanceof final Quantifier quantifier) {
                            quantifier.validate();
                            argument.addPremise(formula);
                        } else {
                            argument.addPremise(formula);
                        }
                    }
                    arguments.put(identifier.getValue(), argument);
                } else if (token instanceof Assert) {
                    final String id = nextToken.getValue();
                    final boolean b = arguments.get(id).isTautology();
                    assertions.put(id, b);
                    System.out.printf(b ?
                            ARGUMENT_IS_TAUTOLOGY : ARGUMENT_IS_NOT_TAUTOLOGY, arguments.get(id).getPremises().get(0));
                } else if (token instanceof Validate) {
                    final String id = nextToken.getValue();
                    final boolean b = arguments.get(id).isValid();
                    validations.put(id, b);
                    System.out.printf(b ? ARGUMENT_VALID : ARGUMENT_INVALID, arguments.get(id));
                } else if (token instanceof Valuate) {
                    final String id = nextToken.getValue();
                    final boolean b = arguments.get(id).valuate(values); // FIXME
                    valuations.put(id, b);
                    System.out.printf(VALUATION, arguments.get(id), b);
                }
            }
        } catch (final NoSuchElementException e) { // this should never happen
            throw new ParserException("Failed to parse line " + line);
        }
    }

    /**
     * Returns the arguments.
     *
     * @return the arguments
     */
    protected Map<String, Argument> getArguments() {
        return arguments;
    }

    /**
     * Returns the assertions.
     *
     * @return the assertions
     */
    protected Map<String, Boolean> getAssertions() {
        return assertions;
    }

    /**
     * Returns the values.
     *
     * @return the values
     */
    protected Map<KPredicate, Boolean> getValues() {
        return values;
    }

    /**
     * Returns the validations.
     *
     * @return the validations
     */
    protected Map<String, Boolean> getValidations() {
        return validations;
    }

    /**
     * Returns the valuations.
     *
     * @return the valuations
     */
    protected Map<String, Boolean> getValuations() {
        return valuations;
    }
}
