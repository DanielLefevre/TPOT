/*
 * ______________________________________________________________________________
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL Redistribution and use in source
 * and binary forms, with or without modification, are permitted provided that
 * the following conditions are met: (1) Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following
 * disclaimer. (2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. (3) The
 * name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission. THIS SOFTWARE
 * IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. Created on 20 f�vr. 2005
 */
package net.jautomata.rationals.properties.relation;

import java.util.List;
import java.util.Set;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.State;


/**
 * An interface for computing equivalences between automata. This interface
 * allows definition of various relation between states.
 * @author nono
 * @version $Id: TransducerRelation.java,v 1.1 2005/03/23 07:22:42 bailly Exp $
 */
public interface Relation {

    /**
     * Sets the context for computing the relation. This method must be called
     * before {@link equivalent(State,State)}.
     * @param a1
     * @param a2
     */
    void setAutomata(Automaton a1, Automaton a2);

    /**
     * Assert the equivalence between two states. This method returns true if
     * and only if the two states are in relation.
     * @param s1
     * @param s2
     * @return true is s1 ~ s2, false otherwise
     */
    boolean equivalence(State s1, State s2);

    /**
     * Asset the equivalence between two set of states. This method returns true
     * if and only if the two states set are equivalent.
     * @param nsa
     *            a Set of State objects from a
     * @param nsb
     *            a Set of State objects from b
     * @return true if nsa is equivalent to nsb
     */
    boolean equivalence(Set nsa, Set nsb);

    /**
     * Returns the trace of labels that lead to an error.
     * @return a List of objects or null.
     */
    List getErrorTrace();
}
