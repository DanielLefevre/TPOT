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
 * Created on 2 juin 2005
 *
 */
package net.jautomata.rationals.distance;

import net.jautomata.rationals.Automaton;

/**
 * Standard euclidean norm based distance.
 * 
 * @author nono
 * @version $Id$
 */
public class DistanceL2 extends Distance {

    /**
     * @param a
     */
    public DistanceL2(Automaton a) {
        super(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.distance.Distance#norm(double[])
     */
    public double norm(double[] vec) {
        double acc = 0.0;
        int l = vec.length;
        for (int i = 0; i < l; i++)
            acc += vec[i] * vec[i];
        return Math.sqrt(acc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.distance.Distance#exponent(double)
     */
    public double exponent(double d) {
        return Math.sqrt(d);

    }

}
