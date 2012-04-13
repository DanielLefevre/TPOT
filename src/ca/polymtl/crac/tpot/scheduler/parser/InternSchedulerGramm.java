package ca.polymtl.crac.tpot.scheduler.parser;

import ca.polymtl.crac.tpot.scheduler.EquivalenceClass;
import ca.polymtl.crac.tpot.scheduler.Scheduler;

public class InternSchedulerGramm implements InternSchedulerGrammConstants {

    static private boolean jj_initialized_once = false;

    /** Generated Token Manager. */
    static public InternSchedulerGrammTokenManager token_source;

    static SimpleCharStream jj_input_stream;

    /** Current token. */
    static public Token token;
    /** Next token. */
    static public Token jj_nt;
    static private int jj_ntk;
    static private int jj_gen;
    static final private int[] jj_la1 = new int[2];
    static private int[] jj_la1_0;

    /** Constructor with InputStream. */
    public InternSchedulerGramm(java.io.InputStream stream) {
        this(stream, null);
    }

    /** Constructor with InputStream and supplied encoding */
    public InternSchedulerGramm(java.io.InputStream stream, String encoding) {
        if (jj_initialized_once) {
            System.out
                    .println("ERROR: Second call to constructor of static parser.  ");
            System.out
                    .println("       You must either use ReInit() or set the JavaCC option STATIC to false");
            System.out.println("       during parser generation.");
            throw new Error();
        }
        jj_initialized_once = true;
        try {
            jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        token_source = new InternSchedulerGrammTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++)
            jj_la1[i] = -1;
    }

    static {
        jj_la1_init_0();
    }
    static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();

    static private int[] jj_expentry;

    static private int jj_kind = -1;

    /** Constructor with generated Token Manager. */
    public InternSchedulerGramm(InternSchedulerGrammTokenManager tm) {
        if (jj_initialized_once) {
            System.out
                    .println("ERROR: Second call to constructor of static parser. ");
            System.out
                    .println("       You must either use ReInit() or set the JavaCC option STATIC to false");
            System.out.println("       during parser generation.");
            throw new Error();
        }
        jj_initialized_once = true;
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++)
            jj_la1[i] = -1;
    }

    /** Constructor. */
    public InternSchedulerGramm(java.io.Reader stream) {
        if (jj_initialized_once) {
            System.out
                    .println("ERROR: Second call to constructor of static parser. ");
            System.out
                    .println("       You must either use ReInit() or set the JavaCC option STATIC to false");
            System.out.println("       during parser generation.");
            throw new Error();
        }
        jj_initialized_once = true;
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new InternSchedulerGrammTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++)
            jj_la1[i] = -1;
    }

    /** Disable tracing. */
    static final public void disable_tracing() {
    }

    /** Enable tracing. */
    static final public void enable_tracing() {
    }

    static final public EquivalenceClass EquivalenceClass()
            throws ParseException {
        EquivalenceClass eqClass = new EquivalenceClass();
        Token action = null;
        jj_consume_token(LPARENT);
        action = jj_consume_token(REG_IDENT);
        eqClass.addEqAction(action.image);
        label_2: while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case COMMA:
                ;
                break;
            default:
                jj_la1[1] = jj_gen;
                break label_2;
            }
            jj_consume_token(COMMA);
            action = jj_consume_token(REG_IDENT);
            eqClass.addEqAction(action.image);
        }
        jj_consume_token(RPARENT);
        {
            if (true)
                return eqClass;
        }
        throw new Error("Missing return statement in function");
    }

    /** Generate ParseException. */
    static public ParseException generateParseException() {
        jj_expentries.clear();
        boolean[] la1tokens = new boolean[13];
        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }
        for (int i = 0; i < 2; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 13; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.add(jj_expentry);
            }
        }
        int[][] exptokseq = new int[jj_expentries.size()][];
        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = jj_expentries.get(i);
        }
        return new ParseException(token, exptokseq, tokenImage);
    }

    /** Get the next Token. */
    static final public Token getNextToken() {
        if (token.next != null)
            token = token.next;
        else
            token = token.next = InternSchedulerGrammTokenManager
                    .getNextToken();
        jj_ntk = -1;
        jj_gen++;
        return token;
    }

    /** Get the specific Token. */
    static final public Token getToken(int index) {
        Token t = token;
        for (int i = 0; i < index; i++) {
            if (t.next != null)
                t = t.next;
            else
                t = t.next = InternSchedulerGrammTokenManager.getNextToken();
        }
        return t;
    }

    static private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;
        if ((oldToken = token).next != null)
            token = token.next;
        else
            token = token.next = InternSchedulerGrammTokenManager
                    .getNextToken();
        jj_ntk = -1;
        if (token.kind == kind) {
            jj_gen++;
            return token;
        }
        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0x400, 0x200,};
    }

    static private int jj_ntk() {
        if ((jj_nt = token.next) == null)
            return (jj_ntk = (token.next = InternSchedulerGrammTokenManager
                    .getNextToken()).kind);
        else
            return (jj_ntk = jj_nt.kind);
    }

    public static Scheduler parseEquivalenceClasses(String file)
            throws ParseException {
        InternSchedulerGramm parser = new InternSchedulerGramm(
                new java.io.StringReader(file));
        Scheduler sched = InternSchedulerGramm.Start();
        return sched;
    }

    /** Reinitialise. */
    static public void ReInit(java.io.InputStream stream) {
        ReInit(stream, null);
    }

    /** Reinitialise. */
    static public void ReInit(java.io.InputStream stream, String encoding) {
        try {
            jj_input_stream.ReInit(stream, encoding, 1, 1);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        InternSchedulerGrammTokenManager.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++)
            jj_la1[i] = -1;
    }

    /** Reinitialise. */
    static public void ReInit(java.io.Reader stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        InternSchedulerGrammTokenManager.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++)
            jj_la1[i] = -1;
    }

    /*****************************************************************************/
    /*
     * high level structure
     * *****************************************************
     */
    /*****************************************************************************/
    static final public Scheduler Start() throws ParseException {
        Scheduler sched = new Scheduler();
        EquivalenceClass eqClass;
        label_1: while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case LPARENT:
                ;
                break;
            default:
                jj_la1[0] = jj_gen;
                break label_1;
            }
            eqClass = EquivalenceClass();
            sched.addEqClass(eqClass);
        }
        jj_consume_token(0);
        {
            if (true)
                return sched;
        }
        throw new Error("Missing return statement in function");
    }

    /** Reinitialise. */
    public static void ReInit(InternSchedulerGrammTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++)
            jj_la1[i] = -1;
    }

}
