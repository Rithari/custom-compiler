import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
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
                error("Syntax error in statlistp");
        }
    }
    public void prog() {
        int lnext_prog = code.newLabel();
        statlist();
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        }
        catch(java.io.IOException e) {
        	System.out.println("IO error\n");
        }
    }

    private void stat() {
        switch(look.tag) {
            case ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(true);
                break;
            case PRINT:
                match(Tag.PRINT);
                match(Tag.PQA);
                exprlist();
                code.emit(OpCode.invokestatic, 1);
                match(Tag.PQC);
                break;
            case READ:
                match(Tag.READ);
                match(Tag.PQA);
                idlist(false);
                match(Tag.PQC);
                break;
            case WHILE:
                match(Tag.WHILE);
                match(Tag.PTA);
                int whileStart= code.newLabel();
                int whileTrue = code.newLabel();
                int whileEnd = code.newLabel();
                code.emitLabel(whileStart);
                bexpr(whileTrue, whileEnd);
                code.emitLabel(whileTrue);
                match(Tag.PTC);
                stat();
                code.emit(OpCode.GOto, whileStart);
                code.emitLabel(whileEnd);
                break;
            case COND:
                match(Tag.COND);
                match(Tag.PQA);
                int condElse = code.newLabel();
                int condEnd = code.newLabel();
                optlist(condEnd);
                match(Tag.PQC);
                if(look.tag == Tag.ELSE) {
                    match(Tag.ELSE);
                    code.emitLabel(condElse);
                    stat();
                }
                code.emitLabel(condEnd);
                match(Tag.END);
                break;
            case PGA:
                match(Tag.PGA);
                statlist();
                match(Tag.PGC);
                break;
        }
    }

    // Create a variable, if it doesn't exist. idlist only does this once where idlistp does it recursively
    private void idlist(boolean isAssign) {
        switch(look.tag) {
	    case ID:
        	int id_addr = st.lookupAddress(((Keyword)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Keyword)look).lexeme,count++);
                }
                if(isAssign) {
                    code.emit(OpCode.istore, id_addr);
                } else {
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, id_addr);
                }
                match(Tag.ID);
                idlistp(isAssign, id_addr);
                break;
    	}
    }

    private void idlistp(boolean isAssign, int id_addrOld) {
        switch(look.tag) {
            case SEMICOLON, PQC, PGC, EOF, END:
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
                    code.emit(OpCode.istore, id_addr);
                } else {
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, id_addr);
                }
                match(Tag.ID);
                idlistp(isAssign, id_addr);
                break;
            default:
                error("Syntax error in idlistp");
        }
    }

    private void expr() {
        switch(look.tag) {
            case NUM:
                int num = ((NumberTok)look).num;
                code.emit(OpCode.ldc, num);
                match(Tag.NUM);
                break;
            case ID:
                int id_addr = st.lookupAddress(((Keyword)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Keyword)look).lexeme,count++);
                }
                code.emit(OpCode.iload, id_addr);
                match(Tag.ID);
                break;
            case SUB:
                match(Tag.SUB);
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case SUM:
                match(Tag.SUM);
                match(Tag.PTA);
                exprlist();
                match(Tag.PTC);
                code.emit(OpCode.iadd);
                break;
            case MUL:
                match(Tag.MUL);
                match(Tag.PTA);
                exprlist();
                match(Tag.PTC);
                code.emit(OpCode.imul);
                break;
            case DIV:
                match(Tag.DIV);
                expr();
                expr();
                code.emit(OpCode.idiv);
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

    private void optlist(int condEnd) {
        optitem(condEnd);
        optlistp(condEnd);
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
        switch(look.tag) {
            case OPTION:
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
                break;
            default:
                error("Syntax error in optitem" + look.tag);
        }
    }
    // Bexpr evaluates the expression and emits the appropriate code
    private void bexpr(int true_label, int false_label) {
        if(look == Keyword.eq) {
            match(Tag.RELOP);
            expr();
            expr();
            code.emit(OpCode.if_icmpeq, true_label);
            code.emit(OpCode.GOto, false_label);
        } else if(look == Keyword.lt) {
            match(Tag.RELOP);
            expr();
            expr();
            code.emit(OpCode.if_icmplt, true_label);
            code.emit(OpCode.GOto, false_label);
        } else if(look == Keyword.gt) {
            match(Tag.RELOP);
            expr();
            expr();
            code.emit(OpCode.if_icmpgt, true_label);
            code.emit(OpCode.GOto, false_label);
        } else if(look == Keyword.le) {
            match(Tag.RELOP);
            expr();
            expr();
            code.emit(OpCode.if_icmple, true_label);
            code.emit(OpCode.GOto, false_label);
        } else if(look == Keyword.ge) {
            match(Tag.RELOP);
            expr();
            expr();
            code.emit(OpCode.if_icmpge, true_label);
            code.emit(OpCode.GOto, false_label);
        } else if(look == Keyword.ne) {
            match(Tag.RELOP);
            expr();
            expr();
            code.emit(OpCode.if_icmpne, true_label);
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

