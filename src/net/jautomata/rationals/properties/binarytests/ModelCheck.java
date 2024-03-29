/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 20 avr. 2005
 *
 */
package net.jautomata.rationals.properties.binarytests;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.properties.unarytests.IsEmpty;
import net.jautomata.rationals.transformations.Complement;
import net.jautomata.rationals.transformations.Mix;
import net.jautomata.rationals.transformations.Pruner;

/**
 * This class implements a basic model-checking algorithm.
 * <p>
 * The first automata is first complemented on its alphabet using the operation
 * {@see rationals.transformations.Complement}. It is then synchronized with
 * the second automaton using the {@see rationals.transformations.Mix}
 * operation.
 * <p>
 * If the langage produced is empty, then the test returns true which means that
 * automaton <code>b</code> contains the language of <code>a</code>. Else,
 * the language produced represents counterexamples of the property modelled by
 * <code>a</code> in <code>b</code>: the test returns false.
 * <p>
 * The resulting automaton can be retrieved using the method
 * {@see #counterExamples()}.
 * 
 * @author nono
 * @version $Id$
 */
public class ModelCheck implements BinaryTest {

    private Automaton cex;

    /*
     * (non-Javadoc)
     * 
     * @see rationals.properties.BinaryTest#test(rationals.Automaton,
     *      rationals.Automaton)
     */
    public boolean test(Automaton a, Automaton b) {
        Automaton ca = new Complement().transform(a);
        cex = new Pruner().transform(new Mix().transform(ca, b));
        if (new IsEmpty().test(cex))
            return true;
        else
            return false;
    }

    /**
     * Return the automaton resulting from this test.
     * 
     * @return an Automaton or null if
     *         {@see #test(rationals.Automaton,rationals.Automaton)}has not
     *         been called yet.
     */
    public Automaton counterExamples() {
        return cex;
    }
}
