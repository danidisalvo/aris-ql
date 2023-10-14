package com.probendi.aris.formula;

import com.probendi.aris.exception.MissingSymbolException;

import java.util.*;

/**
 * A quantifier logic argument.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public class Argument implements WellFormedFormula {

    final private List<WellFormedFormula> premises = new LinkedList<>();
    private WellFormedFormula conclusion;

    /**
     * Creates a new argument.
     */
    public Argument() {
    }

    /**
     * Creates a new argument with the given premises and conclusion.
     *
     * @param conclusion the conclusion
     * @param premises   the premises
     * @throws IllegalArgumentException if conclusion or premises is {@code null}
     */
    public Argument(final WellFormedFormula conclusion, final WellFormedFormula... premises) {
        if (conclusion == null) {
            throw new IllegalArgumentException("conclusion cannot be null");
        }
        if (premises == null) {
            throw new IllegalArgumentException("premises cannot be null or empty");
        }
        this.conclusion = conclusion;
        this.premises.addAll(List.of(premises));
    }

    /**
     * Returns the premises.
     *
     * @return the premises
     */
    public List<WellFormedFormula> getPremises() {
        return premises;
    }

    /**
     * Returns the conclusion.
     *
     * @return the conclusion
     */
    public WellFormedFormula getConclusion() {
        return conclusion;
    }

    /**
     * Sets the conclusion.
     *
     * @param conclusion the conclusion to be set
     */
    public void setConclusion(final WellFormedFormula conclusion) {
        if (conclusion == null) {
            throw new IllegalArgumentException("conclusion cannot be null");
        }
        this.conclusion = conclusion;
    }

    /**
     * Adds the given premise to this argument.
     *
     * @param premise the premise to be added
     */
    public void addPremise(final WellFormedFormula premise) {
        if (premise == null) {
            throw new IllegalArgumentException("premise cannot be null");
        }
        premises.add(premise);
    }

    /**
     * Returns {@code true} if this argument is a tautology.
     *
     * @return {@code true} if the given well-formed formula is a tautology
     */
    public boolean isTautology() {
        throw new UnsupportedOperationException(); // todo implement me
    }

    /**
     * Returns {@code true} if this argument is valid, i.e., if there is a relevant valuation which makes the premises
     * and the negation of the conclusion all true.
     *
     * @return {@code true} if this argument is valid
     * @throws MissingSymbolException if a symbol is not found in the values lookup table
     */
    public boolean isValid() throws MissingSymbolException {
        final List<WellFormedFormula> formulae = new LinkedList<>();
        if (conclusion instanceof ExistentialQuantifier) {
            final Quantifier q = (ExistentialQuantifier) conclusion;
            formulae.add(new UniversalQuantifier(q.variable, new Negation(q.wff)));
        } else {
            formulae.add(new Negation(conclusion));
        }
        formulae.addAll(premises);

        // determine the domain
        final Set<String> dummyNames = new HashSet<>();
        final Set<String> properNames = new HashSet<>();
        final Set<String> unknowns = new HashSet<>();

        int j = 0;
        final String s = this.toString();
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c >= 'm' && c <= 'w') {
                properNames.add(String.valueOf(c));
            } else if (c == '∃') {
                final char c2 = s.charAt(i + 2);
                if (c2 >= 'A' && c2 <= 'Z' || c2 == '∃' || c2 == '∀') {
                    dummyNames.add(String.valueOf((char) ('a' + j++)));
                }
            } else if (c == '∀') {
                unknowns.add(String.valueOf(s.charAt(++i)));
            }
        }

        // add as many names to the domain as the number of unknowns
        if (properNames.isEmpty()) {
            for (int i = 0; i < unknowns.size(); i++) {
                properNames.add(String.valueOf((char) ('m' + i)));
            }
        }

        System.out.printf("domain:     %s\n", properNames);
        System.out.printf("dummyNames: %s\n", dummyNames);
        System.out.printf("unknowns:   %s\n", unknowns);

        final Set<String> domain = new HashSet<>(properNames);
        domain.addAll(dummyNames);

        // iterate over the domain
        for (final String name : domain) {
            System.out.printf("name: %s\n", name);
            final Set<KPredicate> vars = new HashSet<>();
            for (final WellFormedFormula wff : formulae) {
                for (final Condition condition : wff.determineTruthnessConditions()) {
                    for (final Condition atomicCondition : getAtomicConditions(condition)) {
                        vars.add(((AtomicCondition) atomicCondition).getPredicate());
                    }
                }
            }

            // iterate over all possible input variables configurations
            for (final Map<KPredicate, Boolean> values : generateTruthTable(vars, name)) {
                System.out.printf("values: %s\n", values);
                boolean valid = true;
                for (final WellFormedFormula wff : formulae) {
                    // if one formula is false, then move on to the next input variables configuration
                    final boolean b = wff.valuate(values);
                    System.out.printf("%s: %s\n", wff, b);
                    if (b) {
                        valid = false;
                        break;
                    }
                }
                // an input variables configuration which makes the premises and the negated conclusion all true was found
                if (valid) {
                    return false;
                }
            }
        }
        // no configuration was found, hence the argument is valid
        return true;
    }

    @Override
    public List<Condition> determineFalsehoodConditions() {
        throw new UnsupportedOperationException(); // todo: implement me
    }

    @Override
    public List<Condition> determineTruthnessConditions() {
        throw new UnsupportedOperationException(); // todo: implement me
    }

    @Override
    public boolean valuate(final Map<KPredicate, Boolean> values) throws MissingSymbolException {
        throw new UnsupportedOperationException(); // todo implement me
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Argument argument)) {
            return false;
        }
        return getPremises().equals(argument.getPremises()) && Objects.equals(getConclusion(), argument.getConclusion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPremises(), getConclusion());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final WellFormedFormula premise : premises) {
            sb.append(premise).append(", ");
        }
        return sb.isEmpty() ? "" : String.format("%s ∴ %s", sb.substring(0, sb.length() - 2), conclusion);
    }

    private List<Map<KPredicate, Boolean>> generateTruthTable(final Set<KPredicate> vars, final String name) {
        final List<KPredicate> varsList = new LinkedList<>(vars);
        final List<Map<KPredicate, Boolean>> result = new LinkedList<>();
        final int n = varsList.size();
        for (int i = 0; n > 0 && i != (1 << n); i++) {
            final StringBuilder sb = new StringBuilder(Integer.toBinaryString(i));
            while (sb.length() != n) {
                sb.insert(0, '0');
            }
            final Map<KPredicate, Boolean> map = new HashMap<>();
            for (int j = 0; j < n; j++) {
                map.put(varsList.get(j), sb.charAt(j) == '1');
            }

            // iterate over the map
            // assuming that 'name' is 'n', and a predicate 'Fn' is in the map
            // the assign to the predicate of variable, e.g., 'Fx', the value of 'Fn'
            final Map<KPredicate, Boolean> map2 = new HashMap<>();
            for (final KPredicate k : map.keySet()) {
                if (k.hasVariable()) {
                    final Boolean v = map.get(new KPredicate(k.getValue(), name));
                    map2.put(k, v == null ? map.get(k) : v);
                } else {
                    map2.put(k, k.hasVariable() ? map.get(new KPredicate(k.getValue(), name)) : map.get(k));
                }
            }

            result.add(map2);
        }
        return result;
    }

    private List<Condition> getAtomicConditions(final Condition condition) {
        final List<Condition> atomicConditions = new LinkedList<>();
        if (condition instanceof AtomicCondition) {
            atomicConditions.add(condition);
        } else {
            final BinaryCondition b = (BinaryCondition) condition;
            atomicConditions.addAll(getAtomicConditions(b.c1()));
            atomicConditions.addAll(getAtomicConditions(b.c2()));
        }
        return atomicConditions;
    }
}
