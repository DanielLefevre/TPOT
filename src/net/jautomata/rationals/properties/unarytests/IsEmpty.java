package net.jautomata.rationals.properties.unarytests;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.State;

public class IsEmpty {

    public static boolean test(Automaton a) {
        for (State s : a.accessibleStates()) {
            if (s.isTerminal()) {
                return false;
            }
        }
        return true;
    }
}
