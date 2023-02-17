import java.io.*;

public class Validator {
    private final Lexer lex;
    private final BufferedReader pbr;
    private Token look;

    public Validator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(Tag t) {
        if (look.getTag() == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void start() {
        int expr_val;

        expr_val = expr();
        match(Tag.EOF);
        System.out.println(expr_val);
    }

    private int expr() {
        int term_val, exprp_val;

        term_val = term();
        exprp_val = exprp(term_val);

        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val = exprp_i;
        switch (look.tag) {
            case SUM:
                match(Tag.SUM);
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
            case SUB:
                match(Tag.SUB);
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case MUL, DIV, PTA, PTC, NUM, EOF:
                break;
            default:
                error("Syntax error in exprp");
        }
        return exprp_val;
    }

    private int termp(int termp_i) {
        int termp_val = 1, fact_val;

        switch (look.tag) {
            case MUL -> {
                match(Tag.MUL);
                fact_val = fact();
                termp_val = termp(fact_val * termp_i);
            }
            case DIV -> {
                match(Tag.DIV);
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
            }
            case PTA, PTC, NUM, SUM, SUB, EOF -> termp_val = termp_i;
            default -> error("Syntax error in termp");
        }
        return termp_val;
    }

    private int term() {
        int fact_val, termp_val;

        fact_val = fact();
        termp_val = termp(fact_val);

        return termp_val;
    }

    private int fact() {
        int fact_val = 0;

        switch (look.tag) {
            case PTA -> {
                match(Tag.PTA);
                fact_val = expr();
                match(Tag.PTC);
            }
            case NUM -> {
                fact_val = (((NumberTok) look).num);
                match(Tag.NUM);
            }
            default -> error("Syntax error in fact");
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/Users/leoluca/Developer/IdeaProjects/ProgettoLFT/src/valtest.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Validator valutatore = new Validator(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}