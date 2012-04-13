/*
 * Generated By:JavaCC: Do not edit this line.
 * InternSchedulerGrammConstants.java
 */
package ca.polymtl.crac.tpot.scheduler.parser;

/**
 * Token literal values and constants. Generated by
 * org.javacc.parser.OtherFilesGen#start()
 */
public interface InternSchedulerGrammConstants {

    /** End of File. */
    int EOF = 0;
    /** RegularExpression Id. */
    int SINGLE_LINE_COMMENT = 3;
    /** RegularExpression Id. */
    int SPACE = 5;
    /** RegularExpression Id. */
    int EOL = 6;
    /** RegularExpression Id. */
    int MODULE = 7;
    /** RegularExpression Id. */
    int ENDMODULE = 8;
    /** RegularExpression Id. */
    int COMMA = 9;
    /** RegularExpression Id. */
    int LPARENT = 10;
    /** RegularExpression Id. */
    int RPARENT = 11;
    /** RegularExpression Id. */
    int REG_IDENT = 12;

    /** Lexical state. */
    int DEFAULT = 0;
    /** Lexical state. */
    int IN_SINGLE_LINE_COMMENT = 1;

    /** Literal token values. */
    String[] tokenImage = {"<EOF>", "<token of kind 1>", "\"#\"",
            "<SINGLE_LINE_COMMENT>", "<token of kind 4>", "\" \"", "<EOL>",
            "\"module\"", "\"endmodule\"", "\",\"", "\"(\"", "\")\"",
            "<REG_IDENT>",};

}