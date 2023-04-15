# aris-ql
A simple quantifier logic (QL) interpreter inspired by Peter Smith's _An Introduction to Formal Logic_, 2nd edition - https://www.logicmatters.net/ifl/).

**However** (Smith, _An Introduction to Formal Logic_, Secondo Edition, p. 353)
- Q-validity is defined in the predictable way: a QL inference between sen- tences is q-valid if it is truth-preserving on all q-valuations.
- Our QL proof system is sound: so a proof in the system from given sentences as premisses to a sentence as conclusion shows that the inference from the premisses to the conclusion is q-valid.
- We can show a QL inference is q-invalid by finding a countervaluation, a q-valuation which makes the premisses true and conclusion false. But note, q-invalidity doesn’t prove plain invalidity; a q-invalid argument will not be valid in virtue of the way that quantifiers and connectives appear, but it can be valid for some other reason.
- In some simple cases, we can mechanically test a QL inference for q-validity – in particular, when it just involves unary predicates. There is, however, no general method for deciding questions of q-validity.

For example, `aris-ql` erroneously claims that the arguments `∃xFx, ∃xGx ∴ ∃x(Fx ∧ Gx)` is valid.  

Consequently, `aris-ql` must be considered as **Work-In-Progress** and must be used it wisely.

### Unary Operators

**Negation**: `¬`, `!`, or `~`

### Binary Operators

- **Assignment**: `:=`

- **Conjunction**: `∧`, or `&`

- **Disjunction**: `∨`, or `|`

- **Conditional, or Material implication**: `→`, `->`, or `⊃`

- **Therefore**: `∴`, or `therefore`

Binary operators have the same precedence, and _must_ always be enclosed in a pair of round brackets.

### Quantifiers

***Existential quantifier**: `∃`

**Universal quantifier**: `∀`

### Variables

A variable is a late lower case latin letter (i.e., `x`, `y`, or `z`) followed by zero or more primes `'`.

### Dummy Names 

A dummy name is an early lower case latin letter (i.e. from `a` to `l`).

### Proper Names

A proper name is a middle lower case latin letter (i.e. from `m` to `w`).

### Predicates

A predicate is represented by an upper case latin letter.

### Identifiers

An identifier _must_ begin with a lower case latin letter (a-z), and it can contain lower case latin letters and digits 

### Reserved Words

- `argument`
- `assert`
- `false`
- `print`
- `therefore`
- `true`
- `validate`
- `valuate`

## How to Build and Run aris-ql

```
mvn clean package

java -jar target/aris-ql-1.0.jar test.txt
```






