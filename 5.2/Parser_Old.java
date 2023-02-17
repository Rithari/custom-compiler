import java.io.*;

public class Parser_Old {
    private final Lexer lex;
    private final BufferedReader pbr;
    private Token look;

    public Parser_Old(Lexer l, BufferedReader br) {
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
        expr();
        match(Tag.EOF);
    }

    private void expr() {
        term();
        exprp();
    }

    private void exprp() {
        switch (look.tag) {
            case SUM:
                match(Tag.SUM);
                term();
                exprp();
                break;
            case SUB:
                match(Tag.SUB);
                term();
                exprp();
                break;
            case MUL, DIV, PTA, PTC, NUM, EOF:
                break;
            default:
                error("Syntax error in exprp");
        }
    }

    private void term() {
        fact();
        termp();
    }

    private void termp() {
        switch(look.tag) {
            case MUL:
                match(Tag.MUL);
                fact();
                termp();
                break;
            case DIV:
                match(Tag.DIV);
                fact();
                termp();
                break;
            case SUM, SUB, PTA, PTC, NUM, EOF:
                break;
            default:
                error("Syntax error in termp.");
        }
    }

    private void fact() {
        if(look.tag == Tag.NUM) {
            move();
        } else {
            match(Tag.PTA);
            expr();
            match(Tag.PTC);
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/Users/leoluca/Developer/IdeaProjects/ProgettoLFT/src/parsertest.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser_Old parser = new Parser_Old(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}