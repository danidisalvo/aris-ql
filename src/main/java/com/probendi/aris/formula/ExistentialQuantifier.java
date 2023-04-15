package com.probendi.aris.formula;

import com.probendi.aris.exception.MissingSymbolException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the existential quantifier.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class ExistentialQuantifier extends Quantifier implements WellFormedFormula {

    /**
     * Creates a new existential quantifier with the given well-formed formula.
     *
     * @param variable the variable
     * @param wff      the symbol
     * @throws IllegalArgumentException if wff is null or variable is null or empty
     */
    public ExistentialQuantifier(final String variable, final WellFormedFormula wff) {
        super(variable, wff);
    }

    @Override
    public List<Condition> determineFalsehoodConditions() {
        return wff.determineFalsehoodConditions();
    }

    @Override
    public List<Condition> determineTruthnessConditions() {
        return wff.determineTruthnessConditions();
    }

    @Override
    public boolean valuate(final Map<KPredicate, Boolean> values) throws MissingSymbolException {
        return wff.valuate(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExistentialQuantifier that)) {
            return false;
        }
        return variable.equals(that.variable) && wff.equals(that.wff);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, wff);
    }

    @Override
    public String toString() {
        return "∃" + variable + wff;
    }
}
