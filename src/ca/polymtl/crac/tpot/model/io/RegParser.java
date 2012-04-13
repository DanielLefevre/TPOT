package ca.polymtl.crac.tpot.model.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.converters.ConverterException;
import net.jautomata.rationals.converters.Expression;
import ca.polymtl.crac.tpot.model.Model;

/**
 * TODO.
 * @author Daniel Lefevre
 */
public class RegParser extends AbstractParser {

    /**
     * The list of observations.
     */
    private List<Automaton> parsedObservations = new ArrayList<>();
    /**
     * The predicate.
     */
    private Automaton parsedPhi;

    /**
     * Getter.
     * @return the list of observations
     */
    public final List<Automaton> getParsedObservations() {
        return this.parsedObservations;
    }

    /**
     * Getter.
     * @return the predicate
     */
    public final Automaton getParsedPhi() {
        return this.parsedPhi;
    }

    /**
     * Constructor.
     * @param fileNameIn
     *            the name of the file
     */
    public RegParser(final String fileNameIn) {
        super(fileNameIn);
    }

    /**
     * Reads the file, and builds the list of observations, and the predicate.
     * @throws IOException
     *             if an io error occured
     */
    public final void parseFile() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(
                this.getFileName()));

        // Reading all the observation classes (as automata).
        String line = reader.readLine();
        line = reader.readLine();
        while (!line.equals("]")) {
            line = line.trim();
            try {
                this.parsedObservations.add(Expression.fromString(line));
            } catch (ConverterException ex) {
                Model.LOGGER.log(Level.SEVERE, null, ex);
            }
            line = reader.readLine();
        }

        // Reading the predicate.
        line = reader.readLine();
        line = reader.readLine();

        try {
            this.parsedPhi = Expression.fromString(line);
        } catch (ConverterException ex) {
            Model.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
