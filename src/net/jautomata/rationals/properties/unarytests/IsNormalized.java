package net.jautomata.rationals.properties.unarytests;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.State;

/**
 * Tests if an automaton is normalized.
 * @see net.jautomata.rationals.transformations.Normalizer
 * @author nono
 * @version $Id: isNormalized.java,v 1.1 2005/03/23 07:22:42 bailly Exp $
 */
public class IsNormalized {

    public static boolean test(final Automaton a) {
        if (a.initials().size() != 1) {
            return false;
        }
        if (a.terminals().size() != 1) {
            return false;
        }
        State e = a.initials().iterator().next();
        if (a.deltaMinusOne(e).size() > 0) {
            return false;
        }
        e = a.terminals().iterator().next();
        if (a.delta(e).size() > 0) {
            return false;
        }
        return true;
    }
}
