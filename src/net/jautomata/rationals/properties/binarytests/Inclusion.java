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
 * POSSIBILITY OF SUCH DAMAGE. Created on 20 avr. 2005
 */
package net.jautomata.rationals.properties.binarytests;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.properties.unarytests.IsEmpty;
import net.jautomata.rationals.transformations.Complement;
import net.jautomata.rationals.transformations.Intersection;
import net.jautomata.rationals.transformations.Pruner;

/**
 * This class tests the inclusion between two automata. We find the complement
 * of automaton "b" and intersect it with automaton "a". If the result is an
 * empty automaton then "a" is incuded in "b".
 * @author nono
 * @author adeft
 * @version $Id$
 */
public class Inclusion implements BinaryTest {

    /**
     * The automaton resulting from the last test.
     */
    private Automaton cex;

    /*
     * (non-Javadoc)
     * @see rationals.properties.BinaryTest#test(rationals.Automaton,
     * rationals.Automaton)
     */
    @Override
    public final boolean test(final Automaton a, final Automaton b) {
        Complement complement = new Complement();
        // we need to add a's alphabet for b's complement
        complement.setAlphabet(a.alphabet());

        Automaton cb = complement.transform(b);
        this.cex = new Pruner().transform(new Intersection().transform(a, cb));

        return new IsEmpty().test(this.cex);
    }

    /**
     * Return the automaton resulting from this test.
     * @return an Automaton or null if {@see
     *         #test(rationals.Automaton,rationals.Automaton)}has not been
     *         called yet.
     */
    public final Automaton counterExamples() {
        return this.cex;
    }
}
