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
 * Created on 1 avr. 2005
 *
 */
package net.jautomata.rationals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Default synchronization scheme for standard automaton. This class
 * synchronizes the labels of two transitions if they are equal as returned by
 * {@see java.lang.Object#equals(java.lang.Object)}.
 * 
 * @author nono
 * @version $Id$
 */
public class DefaultSynchronization implements Synchronization {

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Synchronization#synchronize(rationals.Transition,
     *      rationals.Transition)
     */
    public Object synchronize(Object t1, Object t2) {
        return t1 == null ? null : (t1.equals(t2) ? t1 : null);
    }

    /* (non-Javadoc)
     * @see rationals.Synchronization#synchronizable(java.util.Set, java.util.Set)
     */
    public Set synchronizable(Set a, Set b) {
        Set r = new HashSet(a);
        r.retainAll(b);
        return r;
    }

    /*
     * TO VERIFY (non-Javadoc)
     * @see rationals.Synchronization#synchronizable(java.util.Collection)
     */
    public Set synchronizable(Collection alphl) {
        Set niou = new HashSet();
        /*
         * synchronization set is the union of pairwise 
         * intersection of the sets in alphl
         */
        for(Iterator i = alphl.iterator();i.hasNext();) {
            Set s = (Set)i.next();
            for(Iterator j = alphl.iterator();j.hasNext();) {
                Set b = (Set)j.next();
                niou.addAll(synchronizable(s,b));
            }
        }
        return niou;
    }

    public boolean synchronizeWith(Object object, Set alph) {
        return alph.contains(object);
    }

}