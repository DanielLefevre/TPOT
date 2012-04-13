package net.jautomata.rationals.converters;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.converters.analyzers.Parser;

public class Expression {

    private Expression() {
    }

    public static final Automaton fromString(final String s)
            throws ConverterException {
        return new Parser(s).analyze();
    }
}
