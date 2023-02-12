import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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
        } else error("syntax error in match");
    }

    public void prog() {
        statlist();
        match(Tag.EOF);
    }

    private void statlist() {
        stat();
        statlistp();
    }

    private void statlistp() {
        switch(look.tag) {
            case PGC, EOF:
                break;
            case SEMICOLON:
                match(Tag.SEMICOLON);
                stat();
                statlistp();
                break;
            default:
                error("Synax error in statlistp");
        }
    }

    private void stat() {
        switch(look.tag) {
            case ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
                break;
            case PRINT:
                match(Tag.PRINT);
                match(Tag.PQA);
                exprlist();
                match(Tag.PQC);
                break;
            case READ:
                match(Tag.READ);
                match(Tag.PQA);
                idlist();
                match(Tag.PQC);
                break;
            case WHILE:
                match(Tag.WHILE);
                match(Tag.PTA);
                bexpr();
                match(Tag.PTC);
                stat();
                break;
            case COND:
                match(Tag.COND);
                match(Tag.PQA);
                optlist();
                match(Tag.PQC);
                if(look.tag == Tag.ELSE) {
                    match(Tag.ELSE);
                    stat();
                    match(Tag.END);
                } else {
                    match(Tag.END);
                }
                break;
            case PGA:
                match(Tag.PGA);
                statlist();
                match(Tag.PGC);
                break;
        }
    }

    private void idlist() {
        match(Tag.ID);
        idlistp();
    }

    private void idlistp() {
        switch(look.tag) {
            case SEMICOLON, PQC, PGC:
                break;
            case COMMA:
                match(Tag.COMMA);
                match(Tag.ID);
                idlistp();
                break;
            default:
                error("Syntax error in idlistp");
        }
    }

    private void optlist() {
        optitem();
        optlistp();
    }

    private void optlistp() {
        // optitem optlistp
        // otherwise error
        switch(look.tag) {
            case PQC:
                break;
            case OPTION:
                optitem();
                optlistp();
                break;
            default:
                error("Syntax error in optlistp");
        }
    }

    private void optitem() {
        switch(look.tag) {
            case OPTION:
                match(Tag.OPTION);
                match(Tag.PTA);
                bexpr();
                match(Tag.PTC);
                match(Tag.DO);
                stat();
                break;
            default:
                error("Syntax error in optitem" + look.tag);
        }
    }

    private void bexpr() {
        switch(look.tag) {
            case RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
            default:
                error("Syntax error in bexpr");
        }
    }

    private void expr() {
        switch(look.tag) {
            case NUM:
                match(Tag.NUM);
                break;
            case ID:
                match(Tag.ID);
                break;
            case SUB:
                match(Tag.SUB);
                expr();
                expr();
                break;
            case SUM:
                match(Tag.SUM);
                match(Tag.PTA);
                exprlist();
                match(Tag.PTC);
                break;
            case MUL:
                match(Tag.MUL);
                match(Tag.PTA);
                exprlist();
                match(Tag.PTC);
                break;
            case DIV:
                match(Tag.DIV);
                expr();
                expr();
                break;
            default:
                error("Syntax error in expr");
        }
    }

    private void exprlist() {
        expr();
        exprlistp();
    }

    private void exprlistp() {
        switch(look.tag) {
            case PQC, PTC:
                break;
            case COMMA:
                match(Tag.COMMA);
                expr();
                exprlistp();
                break;
            default:
                error("Syntax error in exprlistp");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/Users/leoluca/Developer/IdeaProjects/ProgettoLFT/src/parsertest.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}