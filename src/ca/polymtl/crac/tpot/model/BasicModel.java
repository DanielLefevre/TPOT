package ca.polymtl.crac.tpot.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * Extends a Model, with basic functionnalities.
 * @author Olivier Bachard, Daniel Lefevre
 */
public class BasicModel extends Model {

    /**
     * Default constructor.
     */
    public BasicModel() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.Model#buildModel(java.lang.String)
     */
    @Override
    public final void buildModel(final String file)
            throws FileNotFoundException {

        this.setFile(file);
        LOGGER.log(Level.INFO, "Building model : " + file + " ...");

        InputStream autoStream = new FileInputStream(file + ".auto");
        InputStream regStream = new FileInputStream(file + ".reg");

        // Reads automaton.
        this.setOpacity(new Opacity(autoStream, regStream));

        // Validates the datas.
        this.getOpacity().validateData();

        Model.LOGGER.log(Level.INFO,
                "Construction du modele terminée avec succes");
    }
}
