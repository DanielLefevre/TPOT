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
 * Created on 29 mars 2005
 *
 */
package net.jautomata.rationals.converters;

import java.io.IOException;
import java.io.InputStream;

import net.jautomata.rationals.Automaton;


/**
 * An interface for constructing an Automaton from a given stream.
 * This interface is the counterpart of the decoder. It reads data from 
 * a given stream encoding an automaton in a certain format (eg. XML) and 
 * then returns an Automaton object constructed from this data. 
 * 
 * @author nono
 * @version $Id$
 * @see StreamEncoder
 */
public interface StreamDecoder {
    
    /**
     * Construct an Automaton from the given stream.
     * The encoding is implementation specific and the basic contract is
     * that a certain decoder must be able to understand data from a 
     * compatible encoder.
     * 
     * @param is the stream to read data from. The caller is responsible for
     * closing the stream.
     * @return a new Automaton object.
     * @throws IOException if something bad happens in the underlying stream.
     * @see StreamEncoder.output(rationals.Automaton, java.io.OutputStream)
     */
    public Automaton input(InputStream is) throws IOException;

}