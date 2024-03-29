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
 * Created on 10 mai 2005
 *
 */
package net.jautomata.rationals.algebra;

/**
 * An interface implemented by objects that can be coefficients of a 
 * Matrix.
 * <p>
 * A semi-ring is a structure <code>(R,+,*,0,1)</code> such that:
 * <ol>
 * <li><code>(R,+,0)</code> is a commutative monoid </li>
 * <li><code>(R,*,1)</code> is a monoid </li>
 * <li><code>x*(y+z) = x*y + x*z</code> and <code>(y+z)*x = y*x + z*x</code> : multiplication
 * is distributive with respect to addition </li>
 * <li><code>x*0 = 0*x = 0</code>: 0 is an absorbing element for *</li>
 * </ol>
 * 
 * @author nono
 * @version $Id$
 * @see Matrix
 */
public interface SemiRing {

    /**
     * Addition of a Semi-ring element with another element.
     * 
     * @param s1
     * @param s2
     * @return
     */
    public SemiRing plus(SemiRing s2);
    
    /**
     * Multiplication of semiring element with another element.
     * 
     * @param s1
     * @param s2
     * @return
     */
    public SemiRing mult(SemiRing s2);
    
    /**
     * Neutral element for multiplication.
     * 
     * @return
     */
    public SemiRing one();
    
    /**
     * Neutral element for addition.
     * 
     * @return
     */
    public SemiRing zero();
    
}
