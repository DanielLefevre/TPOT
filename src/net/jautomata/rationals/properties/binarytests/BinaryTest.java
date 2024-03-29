/*______________________________________________________________________________
*
* Copyright 2004 Arnaud Bailly - NORSYS/LIFL
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
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
* HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
* STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
* OF THE POSSIBILITY OF SUCH DAMAGE.
*______________________________________________________________________________
*
* Created on Sep 21, 2004
* 
*/
package net.jautomata.rationals.properties.binarytests;

import net.jautomata.rationals.Automaton;

/**
 * Interface describing binary tests (ie. tests between two automata).
 * 
 * @author nono
 * @version $Id: BinaryTest.java,v 1.1 2005/03/23 07:22:42 bailly Exp $
 */
public interface BinaryTest {

  /**
   * Tests that some predicate is true for two given automata.
   * 
   * @param a
   * @param b
   * @return
   */
  public boolean test(Automaton a, Automaton b);
  
}

/* 
 * $Log: BinaryTest.java,v $
 * Revision 1.1  2005/03/23 07:22:42  bailly
 * created transductions package
 * corrected EpsilonRemover
 * added some tests
 * removed DirectedGRaph Interface from Automaton
 *
 * Revision 1.1  2004/09/21 11:50:28  bailly
 * added interface BinaryTest
 * added class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 *
*/