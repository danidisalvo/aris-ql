package com.probendi.aris.formula;

import com.probendi.aris.exception.MissingSymbolException;
import com.probendi.aris.token.ProperName;
import com.probendi.aris.token.Token;
import com.probendi.aris.token.Variable;

import java.util.*;

/**
 * Represents a k-ary predicate.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class KPredicate implements WellFormedFormula {

    private final List<String> terms = new LinkedList<>();

    private final String value;

    /**
     * Creates a new k-ary predicate with the given symbol and term.
     *
     * @param value the predicate's symbol
     * @param term  the term
     * @throws IllegalArgumentException if value or terms is {@code null} or empty
     */
    public KPredicate(final String value, final String term) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }
        if (term == null || term.isBlank()) {
            throw new IllegalArgumentException("term cannot be null or empty");
        }
        this.value = value;
        this.terms.add(term);
    }

    /**
     * Creates a new k-ary predicate with the given symbol and terms.
     *
     * @param value the predicate's symbol
     * @param terms the terms
     * @throws IllegalArgumentException if value or terms is {@code null} or empty
     */
    public KPredicate(final String value, final List<String> terms) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }
        if (terms == null || terms.isEmpty()) {
            throw new IllegalArgumentException("terms cannot be null or empty");
        }
        this.value = value;
        this.terms.addAll(terms);
    }

    String getValue() {
        return value;
    }

    List<String> getTerms() {
        return terms;
    }

    boolean hasVariable() {
        return getTerms().size() == 1 && getTerms().get(0).charAt(0) >= 'x';
    }

    @Override
    public List<Condition> determineFalsehoodConditions() {
        return List.of(new AtomicCondition(this).setFalse());
    }

    @Override
    public List<Condition> determineTruthnessConditions() {
        return List.of(new AtomicCondition(this).setTrue());
    }

    @Override
    public boolean valuate(final Map<KPredicate, Boolean> values) throws MissingSymbolException {
        if (values == null) {
            throw new IllegalArgumentException("values cannot be null or empty");
        }
        if (!values.containsKey(this)) {
            throw new MissingSymbolException(this.toString());
        }
        return values.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KPredicate that)) {
            return false;
        }
        return terms.equals(that.terms) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terms, value);
    }

    @Override
    public String toString() {
        return value + terms;
    }

    /**
     * Parses the given tokens and creates a new k-ary predicate with the given symbol.
     *
     * @param symbol the predicate's symbol
     * @param tokens the tokens to be parsed
     * @return the parsed k-predicate
     */
    public static KPredicate parse(final String symbol, final Queue<Token> tokens) {
        final List<String> terms = new LinkedList<>();
        while (tokens.peek() instanceof ProperName || tokens.peek() instanceof Variable) {
            terms.add(tokens.remove().getValue());
        }
        return new KPredicate(symbol, terms);
    }
}
