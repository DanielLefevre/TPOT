package ca.polymtl.crac.tpot.model.io;

import java.io.FileInputStream;
import java.io.IOException;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.converters.JAutoCodec;

/**
 * TODO.
 * @author Daniel Lefevre
 */
public class AutoParser extends AbstractParser {

    /**
     * The read automaton.
     */
    private Automaton parsedAutomaton;

    /**
     * Getter.
     * @return the read automaton
     */
    public final Automaton getParsedAutomaton() {
        return this.parsedAutomaton;
    }

    /**
     * Constructor.
     * @param fileNameIn
     *            the name of the file
     */
    public AutoParser(final String fileNameIn) {
        super(fileNameIn);
    }

    /**
     * Reads the file, and builds this automaton. Uses JAutoCodec.
     * @throws IOException
     *             if an io error occured
     */
    public final void parseFile() throws IOException {
        this.parsedAutomaton = new JAutoCodec().input(new FileInputStream(this
                .getFileName()));
    }
}
