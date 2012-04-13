package ca.polymtl.crac.tpot.model;

import java.io.IOException;
import java.util.logging.Level;

import ca.polymtl.crac.tpot.model.Opacity.IncorrectDataException;
import ca.polymtl.crac.tpot.model.io.AutoParser;
import ca.polymtl.crac.tpot.model.io.RegParser;

/**
 * Extends a Model, with basic functionnalities.
 * @author Olivier Bachard, Daniel Lefevre
 */
public class BasicModel extends Model {

    /**
     * Constructor.
     * @param fileIn
     *            the name of the model to find the names of the files (+
     *            ".auto", and + ".reg")
     */
    public BasicModel(final String fileIn) {
        super(fileIn);
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.Model#buildModel(java.lang.String)
     */
    @Override
    public final void buildModel() throws IncorrectDataException, IOException {
        LOGGER.log(Level.INFO, "Building model : " + this.getFile() + ".");

        // Parses auto file.
        AutoParser autoParser = new AutoParser(this.getFile() + ".auto");
        autoParser.parseFile();

        // Parses reg file.
        RegParser regParser = new RegParser(this.getFile() + ".reg");
        regParser.parseFile();

        // Creates the opacity.
        this.setOpacity(new Opacity(autoParser.getParsedAutomaton(), regParser
                .getParsedObservations(), regParser.getParsedPhi()));

        // Validates the datas.
        this.getOpacity().validateData();

        Model.LOGGER.log(Level.INFO, "Model building finished successfully.");
    }
}
