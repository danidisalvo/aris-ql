package com.probendi.aris.formula;

import com.probendi.aris.exception.ParserException;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A quantifier.
 * <p>
 * {@code @Copyright}, 2023 Daniele Di Salvo
 */
public abstract class Quantifier implements WellFormedFormula {

    private static final String VAR_MISMATCH = "Variables mismatch in '%s'";
    private static final String VAR_REUSED = "The variable '%s' is reused in formula '%s'";

    /**
     * The quantifier's variable.
     */
    protected final String variable;
    /**
     * The quantifier's well-formed formula.
     */
    protected final WellFormedFormula wff;

    /**
     * Creates a new quantifier with the given well-formed formula.
     *
     * @param variable the variable
     * @param wff      the well-formed formula
     * @throws IllegalArgumentException if wff is null or variable is null or empty
     */
    public Quantifier(final String variable, final WellFormedFormula wff) {
        if (variable == null || variable.isBlank()) {
            throw new IllegalArgumentException("wff cannot be null");
        }
        if (wff == null) {
            throw new IllegalArgumentException("wff cannot be null");
        }
        this.variable = variable;
        this.wff = wff;
    }

    /**
     * Ensures that the quantifiers of well-formed formula do not reuse the same variable and that there are no
     * unbound variables.
     *
     * @throws ParserException if validation fails
     */
    public void validate() throws ParserException {
        final Set<String> varsInQuantifiers = new HashSet<>();
        varsInQuantifiers.add(variable);
        final WellFormedFormula targetWwf;
        try {
            // confirm that the no variable is reused
            targetWwf = isVarReused(varsInQuantifiers);
        } catch (final ParserException e) {
            throw new ParserException(String.format(VAR_REUSED, e.getMessage(), beautifyToString()));
        }

        // the variables in the wff must be the same variables of the quantifiers
        final Set<String> varsInWff = new HashSet<>();

        final Pattern p = Pattern.compile("\\[(.*?)]");
        final Matcher m = p.matcher(targetWwf.toString());
        while (m.find()) {
            final String[] strings = m.group(1).split(",");
            for (final String s : strings) {
                varsInWff.add(s.trim());
            }
        }

        if (!varsInQuantifiers.equals(varsInWff)) {
            throw new ParserException(String.format(VAR_MISMATCH, beautifyToString()));
        }
    }

    private WellFormedFormula isVarReused(final Set<String> varsInQuantifiers) throws ParserException {
        if (wff instanceof final Quantifier quantifier) {
            if (varsInQuantifiers.contains(quantifier.variable)) {
                throw new ParserException(quantifier.variable);
            }
            varsInQuantifiers.add(quantifier.variable);
            return quantifier.isVarReused(varsInQuantifiers);
        } else {
            return wff;
        }
        //throw new ParserException("Unexpected parsing exception");
    }

    private String beautifyToString() {
        return this.toString().replace("[", "")
                .replace("]", "").replace(", ", "");
    }
}
