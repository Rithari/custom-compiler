import java.io.*;
import java.util.Objects;

public class Translator {
    private final Lexer lex;
    private final BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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

    private void statlist() {
        switch (look.tag) {
            case ASSIGN, PRINT, READ, WHILE, COND, PGA -> {
                stat();
                statlistp();
            }
            default -> error("Syntax error in statlist");
        }
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
                error("Syntax error in statlistp");
        }
    }
    public void prog() {
        switch (look.tag) {
            case ASSIGN, PRINT, READ, WHILE, COND, PGA -> {
                int lnext_prog = code.newLabel();
                statlist();
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                } catch (IOException e) {
                    System.out.println("IO error\n");
                }
            }
            default -> error("Syntax error in prog");
        }
    }

    private void stat() {
        switch (look.tag) {
            case ASSIGN -> {
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(true);
            }
            case PRINT -> {
                match(Tag.PRINT);
                match(Tag.PQA);
                exprlist(OpCode.invokestatic);
                match(Tag.PQC);
            }
            case READ -> {
                match(Tag.READ);
                match(Tag.PQA);
                idlist(false);
                match(Tag.PQC);
            }
            case WHILE -> {
                match(Tag.WHILE);
                match(Tag.PTA);
                int whileStart = code.newLabel();
                int whileTrue = code.newLabel();
                int whileEnd = code.newLabel();
                code.emitLabel(whileStart);
                bexpr(whileTrue, whileEnd);
                code.emitLabel(whileTrue);
                match(Tag.PTC);
                stat();
                code.emit(OpCode.GOto, whileStart);
                code.emitLabel(whileEnd);
            }
            case COND -> {
                match(Tag.COND);
                match(Tag.PQA);
                int condEnd = code.newLabel();
                optlist(condEnd);
                match(Tag.PQC);
                condElse();
                code.emitLabel(condEnd);
                match(Tag.END);
            }
            case PGA -> {
                match(Tag.PGA);
                statlist();
                match(Tag.PGC);
            }
            default -> error("Syntax error in else");
        }
    }

    private void condElse() {
        if(look.tag == Tag.ELSE) {
            move();
            stat();
        } else if (look.tag != Tag.END) {
            error("Syntax error in condElse");
        }
    }

    // Create a variable, if it doesn't exist. idlist only does this once where idlistp does it recursively
    private void idlist(boolean isAssign) {
        if (Objects.requireNonNull(look.tag) == Tag.ID) {
            int id_addr = st.lookupAddress(((Keyword) look).lexeme);
            if (id_addr == -1) {
                id_addr = count;
                st.insert(((Keyword) look).lexeme, count++);
            }
            if (!isAssign) {
                code.emit(OpCode.invokestatic, 0);
            }
            code.emit(OpCode.istore, id_addr);
            match(Tag.ID);
            idlistp(isAssign, id_addr);
        }
        else {
            error("Syntax error in idlist");
        }
    }

    private void idlistp(boolean isAssign, int id_addrOld) {
        switch(look.tag) {
            case SEMICOLON, PQC, PGC, EOF, END, OPTION:
                break;
            case COMMA:
                match(Tag.COMMA);
                int id_addr = st.lookupAddress(((Keyword)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Keyword)look).lexeme,count++);
                }
                if(isAssign) {
                    code.emit(OpCode.iload, id_addrOld);
                } else {
                    code.emit(OpCode.invokestatic, 0);
                }
                code.emit(OpCode.istore, id_addr);
                match(Tag.ID);
                idlistp(isAssign, id_addr);
                break;
            default:
                error("Syntax error in idlistp");
        }
    }

    private void expr() {
        switch (look.tag) {
            case NUM -> {
                int num = ((NumberTok) look).num;
                code.emit(OpCode.ldc, num);
                match(Tag.NUM);
            }
            case ID -> {
                Keyword kw = (Keyword) look;
                int id_addr = st.lookupAddress(kw.lexeme);
                if (id_addr == -1) {
                    error("Variable \"" + kw.lexeme + "\" not initialized.");
                }
                code.emit(OpCode.iload, id_addr);
                match(Tag.ID);
            }
            case SUB -> {
                match(Tag.SUB);
                expr();
                expr();
                code.emit(OpCode.isub);
            }
            case SUM -> {
                match(Tag.SUM);
                match(Tag.PTA);
                exprlist(OpCode.iadd);
                match(Tag.PTC);
            }
            case MUL -> {
                match(Tag.MUL);
                match(Tag.PTA);
                exprlist(OpCode.imul);
                match(Tag.PTC);
            }
            case DIV -> {
                match(Tag.DIV);
                expr();
                expr();
                code.emit(OpCode.idiv);
            }
            default -> error("Syntax error in expr");
        }
    }

    private void exprlist(OpCode opCode) {
        switch (look.tag) {
            case ID, NUM, SUM, SUB, MUL, DIV -> {
                expr();
                if (opCode == OpCode.invokestatic) {
                    code.emit(OpCode.invokestatic, 1);
                }
                exprlistp(opCode);
            }
            default -> error("Syntax error in exprlist");
        }
    }

    private void exprlistp(OpCode opCode) {
        switch(look.tag) {
            case PQC, PTC:
                break;
            case COMMA:
                match(Tag.COMMA);
                expr();
                code.emit(opCode, 1);
                exprlistp(opCode);
                break;
            default:
                error("Syntax error in exprlistp");
        }
    }

    private void optlist(int condEnd) {
        if (Objects.requireNonNull(look.tag) == Tag.OPTION) {
            optitem(condEnd);
            optlistp(condEnd);
        } else {
            error("Syntax error in optlist");
        }
    }
    private void optlistp(int condEnd) {
        // optitem optlistp
        // otherwise error
        switch(look.tag) {
            case PQC:
                break;
            case OPTION:
                optitem(condEnd);
                optlistp(condEnd);
                break;
            default:
                error("Syntax error in optlistp");
        }
    }
    private void optitem(int condEnd) {
        if (Objects.requireNonNull(look.tag) == Tag.OPTION) {
            int true_label = code.newLabel();
            int false_label = code.newLabel();
            match(Tag.OPTION);
            match(Tag.PTA);
            bexpr(true_label, false_label);
            match(Tag.PTC);
            match(Tag.DO);
            code.emitLabel(true_label);
            stat();
            code.emit(OpCode.GOto, condEnd);
            code.emitLabel(false_label);
        } else {
            error("Syntax error in optitem" + look.tag);
        }
    }
    // Bexpr evaluates the expression and emits the appropriate code
    private void bexpr(int true_label, int false_label) {
        if (Objects.requireNonNull(look.tag) == Tag.RELOP) {
            Keyword relop = (Keyword) look;
            move();
            expr();
            expr();
            switch (relop.lexeme) {
                case "==" -> code.emit(OpCode.if_icmpeq, true_label);
                case "<" -> code.emit(OpCode.if_icmplt, true_label);
                case ">" -> code.emit(OpCode.if_icmpgt, true_label);
                case "<=" -> code.emit(OpCode.if_icmple, true_label);
                case ">=" -> code.emit(OpCode.if_icmpge, true_label);
                case "<>" -> code.emit(OpCode.if_icmpne, true_label);
            }
            code.emit(OpCode.GOto, false_label);
        } else {
            error("Syntax error in bexpr");
        }
    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/Users/leoluca/Developer/IdeaProjects/ProgettoLFT/2.1 - 5.1/testTrans.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

