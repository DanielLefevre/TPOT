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
 * Created on 18 avr. 2005
 *
 */
package rationals.ioautomata.testing;

import java.util.Iterator;
import java.util.Set;

import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOTransition;

/**
 * Base class for testing IOAutomaton.
 * Provides some utilities to be used by underlying classes.
 * 
 * @author nono
 * @version $Id$
 */
public abstract class AbstractIOTester implements IOAutomataTester {

    /**
     * Remove all input transitions from this set of transitions
     * 
     * @param trs
     */
    public void filterInputs(Set trs) {
        for (Iterator i = trs.iterator(); i.hasNext();)
            if (((IOTransition) i.next()).getType() != IOAlphabetType.INPUT)
                i.remove();
    }

    /**
     * Remove all output transitions from this set of transitions
     * 
     * @param trs
     */
    public void filterOutputs(Set trs) {
        for (Iterator i = trs.iterator(); i.hasNext();)
            if (((IOTransition) i.next()).getType() != IOAlphabetType.OUTPUT)
                i.remove();
    }

    static class Move {
    
        static final Move out = new Move("out");
    
        static final Move reset = new Move("reset");
    
        static final Move terminate = new Move("terminate");
    
        Object move;
    
        Move(Object o) {
            this.move = o;
        }
    }


    
}
