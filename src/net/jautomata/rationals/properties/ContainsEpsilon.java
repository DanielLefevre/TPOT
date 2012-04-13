package net.jautomata.rationals.properties;

import java.util.Set;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.State;
import net.jautomata.rationals.transformations.TransformationsToolBox;


/**
 * Checks whether an automaton recognizes the empty word. This test assumes that
 * the tested automaton does not contain epsilon (ie. <code>null</code>)
 * transitions.
 * @author nono
 * @version $Id: ContainsEpsilon.java,v 1.1 2005/03/23 07:22:42 bailly Exp $
 */
public class ContainsEpsilon {

    public static boolean test(final Automaton a) {
        Set<State> s = a.getStateFactory().stateSet();
        for (State st : a.initials()) {
            if (st.isTerminal()) {
                return true;
            }
            s.add(st);
            /* compute epsilon closure */
            Set cl = TransformationsToolBox.epsilonClosure(s, a);
            if (TransformationsToolBox.containsATerminalState(cl)) {
                return true;
            }
        }
        return false;
    }
}
