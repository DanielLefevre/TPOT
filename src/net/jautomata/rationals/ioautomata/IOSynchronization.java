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
package rationals.ioautomata;

import java.util.Collection;
import java.util.Set;

import rationals.Synchronization;

/**
 * A synchronization between input/output alphabets.
 * This implementation of Synchronization interface expects instances 
 * of {@see rationals.ioautomat.IOTransition}. It returns the label on 
 * transition if one of the transition is input and the other is output.
 * 
 * @author nono
 * @version $Id$
 */
public class IOSynchronization implements Synchronization {

    /* (non-Javadoc)
     * @see rationals.Synchronization#synchronize(rationals.Transition, rationals.Transition)
     */
    public Object synchronize(Object t1, Object t2) {
            if (t1 == null || t2 == null)
                return null;
            IOTransition.IOLetter io1 = (IOTransition.IOLetter)t1;
            IOTransition.IOLetter io2 = (IOTransition.IOLetter)t2;
            if(!io1.label.equals(io2.label))
                return null;
            switch (io1.type.type) {
            case 0:
                if(io2.type == IOAlphabetType.OUTPUT)
                    return io1.label;
            case 1:
                if(io2.type == IOAlphabetType.INPUT)
                    return io1.label;
            case 2:
                return null;
            }
            return null;
    }

    /* TODO
     * @see rationals.Synchronization#synchronizable(java.util.Set, java.util.Set)
     */
    public Set synchronizable(Set a, Set b) {
        return null;
    }

    public Set synchronizable(Collection alphl) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean synchronizeWith(Object object, Set alph) {
        // TODO Auto-generated method stub
        return false;
    }

}
