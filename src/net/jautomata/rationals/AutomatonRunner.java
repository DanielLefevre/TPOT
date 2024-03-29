/*______________________________________________________________________________
*
* Copyright 2003 Arnaud Bailly - NORSYS/LIFL
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
* Created on Jul 23, 2004
* 
*/
package net.jautomata.rationals;

/**
 * An interface for objects running automatons
 * <p>
 * This interface essentially provide a way to communicate with {@link AutomatonRunListener}
 * objects for notifying run events
 * 
 * @author nono
 * @version $Id: AutomatonRunner.java 884 2005-03-23 12:33:25Z bailly $
 */
public interface AutomatonRunner {

    /**
     * Adds a listener to this runner
     * 
     * @param l the listener to add - may no be null
     */
    public void addRunListener(AutomatonRunListener l);
    
    /**
     * Remove a listener from this runner
     * 
     * @param l the listener to remove
     */
    public void removeRunListener(AutomatonRunListener l);
}

/* 
 * $Log: AutomatonRunner.java,v $
 * Revision 1.1  2004/07/23 11:59:17  bailly
 * added listener interfaces
 *
*/