package com.probendi.aris.formula;

import com.probendi.aris.exception.MissingSymbolException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An atomic condition.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class AtomicCondition implements Condition, WellFormedFormula {

    private final KPredicate predicate;
    private Boolean boolValue;

    /**
     * Creates a new atomic condition with the given predicate.
     *
     * @param predicate the predicate
     */
    public AtomicCondition(final KPredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * Returns the predicate.
     *
     * @return the predicate
     */
    public KPredicate getPredicate() {
        return predicate;
    }

    /**
     * Sets this atomic condition to {@code false}.
     *
     * @return this atomic condition.
     */
    public AtomicCondition setFalse() {
        boolValue = false;
        return this;
    }

    /**
     * Sets this atomic condition to {@code true}.
     *
     * @return this atomic condition.
     */
    public AtomicCondition setTrue() {
        boolValue = true;
        return this;
    }

    @Override
    public List<Condition> determineFalsehoodConditions() {
        return List.of(new AtomicCondition(predicate).setFalse());
    }

    @Override
    public List<Condition> determineTruthnessConditions() {
        return List.of(new AtomicCondition(predicate).setTrue());
    }

    @Override
    public boolean valuate(final Map<KPredicate, Boolean> values) throws MissingSymbolException {
        if (values == null) {
            throw new IllegalArgumentException("values cannot be null or empty");
        }
        if (!values.containsKey(predicate)) {
            throw new MissingSymbolException(predicate.toString());
        }
        return values.get(predicate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtomicCondition that)) return false;
        return getPredicate().equals(that.getPredicate()) && boolValue.equals(that.boolValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPredicate(), boolValue);
    }

    @Override
    public String toString() {
        return boolValue == null ? predicate.toString() : (predicate + "=" + boolValue);
    }
}
