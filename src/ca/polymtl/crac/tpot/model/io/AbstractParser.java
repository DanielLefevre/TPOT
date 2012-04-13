package ca.polymtl.crac.tpot.model.io;

/**
 * TODO.
 * @author Daniel
 */
public abstract class AbstractParser {

    /**
     * The name of the file.
     */
    private String fileName;

    /**
     * Getter.
     * @return the name of the file
     */
    public final String getFileName() {
        return this.fileName;
    }

    /**
     * Constructor.
     * @param fileNameIn
     *            the name of the file to parse
     */
    public AbstractParser(final String fileNameIn) {
        this.fileName = fileNameIn;
    }
}
