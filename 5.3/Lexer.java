import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        switch (peek) {
            case '=': {
                readch(br);
                if(peek == '=')  {
                    peek = ' ';
                    return Keyword.eq;
                }
                else {
                    System.err.println("Erroneous character" + " after = : " + peek );
                    return null;
                }
            }
            case '<': {
                readch(br);
                if(peek == '>') {
                    peek = ' ';
                    return Keyword.ne;
                } else if(peek == '=') {
                    peek = ' ';
                    return Keyword.le;
                } else {
                    return Keyword.lt;
                }
            }
            case '>': {
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Keyword.ge;
                } else {
                    return Keyword.gt;
                }
            }
            case '!':
                peek = ' ';
                return Token.not;
            case '.': {
                System.err.println("Invalid character '"+peek+"'");
                return null;
            }
            case ',': {
                peek = ' ';
                return Token.comma;
            }
            case ';': {
                peek = ' ';
                return Token.semicolon;
            }
            case '/': {
                readch(br);
                if(peek == '/') {
                    // Ignore everything until the end of the line
                    while(peek != '\n') {
                        readch(br);
                    }
                    line++;
                    return lexical_scan(br);
                } else if(peek == '*') {
                    if(!validateComment(br)) {
                        System.err.println("Garbage comment");
                        return null;
                    }
                    else {
                        peek = ' ';
                        return lexical_scan(br);
                    }

                } else {
                    return Token.div;
                }
            }
            case '*': {
                peek = ' ';
                return Token.mul;
            }
            case '-': {
                peek = ' ';
                return Token.sub;
            }
            case '+': {
                peek = ' ';
                return Token.sum;
            }
            case '(': {
                peek = ' ';
                return Token.pta;
            }
            case ')': {
                peek = ' ';
                return Token.ptc;
            }
            case '[': {
                peek = ' ';
                return Token.pqa;
            }
            case ']': {
                peek = ' ';
                return Token.pqc;
            }
            case '{': {
                peek = ' ';
                return Token.pga;
            }
            case '}': {
                peek = ' ';
                return Token.pgc;
            }
            case '|':
                readch(br);
                if(peek == '|') {
                    peek = ' ';
                    return Keyword.or;
                } else {
                    System.err.println("Erroneous character" + " after | : " + peek );
                    return null;
                }
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Keyword.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case (char)-1:
                return Token.eof;
            default:
                if (Character.isLetter(peek) || peek == '_') {
                    // Build the string with the sequence of characters
                    StringBuilder toCheck = new StringBuilder();
                    while(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                        toCheck.append(peek);
                        try {
                            br.mark(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } /* *///////////////////////////
                        readch(br);
                    }

                    String toCheckString = toCheck.toString();

                    // go back one character on the reader
                    // so that the next call to readch() will
                    // read the correct character
                    try {
                        br.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Validate the string using a DFA
                    if(validateString(toCheckString)) {
                        // If our valid string has at least 1 digit then it's an identifier and not a number
                        if(toCheckString.matches(".*\\d.*")) {
                            peek = ' ';
                            return new Keyword(Tag.ID, toCheckString);
                        }
                        // If our valid string is a keyword then return the keyword, otherwise return null
                        switch (toCheckString) {
                            case "assign":
                                peek = ' ';
                                return Keyword.assign;
                            case "to":
                                peek = ' ';
                                return Keyword.to;
                            case "option":
                                peek = ' ';
                                return Keyword.option;
                            case "conditional":
                                peek = ' ';
                                return Keyword.conditional;
                            case "else":
                                peek = ' ';
                                return Keyword.elsetok;
                            case "while":
                                peek = ' ';
                                return Keyword.whiletok;
                            case "do":
                                peek = ' ';
                                return Keyword.dotok;
                            case "begin":
                                peek = ' ';
                                return Keyword.begin;
                            case "end":
                                peek = ' ';
                                return Keyword.end;
                            case "print":
                                peek = ' ';
                                return Keyword.print;
                            case "read":
                                peek = ' ';
                                return Keyword.read;
                            default:
                                peek = ' ';
                                return new Keyword(Tag.ID, toCheckString);
                        }
                    } else {
                        System.err.println("Erroneous character: " + peek);
                        return null;
                    }

                } else if (Character.isDigit(peek)) {
                    // Build the string with the sequence of characters
                    StringBuilder toCheck = new StringBuilder();
                    while(Character.isDigit(peek)) {
                        toCheck.append(peek);
                        try {
                            br.mark(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        readch(br);
                    }
                    String toCheckString = toCheck.toString();

                    try {
                        br.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Validate the string using a DFA
                    if(validateNumber(toCheckString)) {
                        peek = ' ';
                        return new NumberTok(toCheckString);
                    } else {
                        System.err.println("Erroneous character: " + peek);
                        return null;
                    }
                } else {
                        System.err.println("Erroneous character: "
                                + peek );
                        return null;
                }
         }
    }
    // DFA: check if the comment start finishes or not if not it's just div, mult and not a comment. For single lines
    // simply continue until the
    // Do this check if the character after the comment start is a div or a mult character
    boolean validateComment(BufferedReader br) {
        int state = 0;
        // while not end of file or */ is found (the latter is state 2)
        while(state != 2 && peek != (char)-1) {
            switch (state) {
                case 0:
                    if (peek == '*') state = 1;
                    else state = 0;
                    break;
                case 1:
                    if (peek == '*') state = 1;
                    else if (peek == '/') state = 2;
                    else state = 0;
                    break;
            }
            readch(br);
        }
        return state == 2;
    }

    boolean validateString(String toCheck) {
        int state = 0;

        for (int i = 0; i < toCheck.length(); i++) {
            char c = toCheck.charAt(i);
            switch (state) {
                case 0:
                    if(c == '_') state = 1;
                    else if (Character.isLetter(c)) state = 2;
                    else state = -1;
                    break;
                case 1:
                    if (c == '_') continue;
                    else if (Character.isLetter(c) || Character.isDigit(c)) state = 2;
                    else state = -1;
                    break;
                case 2:
                    if (Character.isLetter(c) || Character.isDigit(c) || c == '_') continue;
                    else state = -1;
                    break;
            }
        }

        return state == 2;
    }

    boolean validateNumber(String toCheck) {
        int state = 0;

        // Iterate over string and check each character
        for (int i = 0; i < toCheck.length(); i++) {
            char c = toCheck.charAt(i);

            switch (state) {
                case 0 -> {
                    if (c == '0') state = 2;
                    else if (c >= '1' && c <= '9') state = 1;
                    else state = -1;
                }
                case 1 -> {
                    if (c >= '0' && c <= '9') continue;
                    else state = -1;
                }
                case 2 -> state = -1;
            }
        }
        return state == 1 || state == 2;
    }

    public static void main(String[] args) {
       Lexer lex = new Lexer();
        String path = "/Users/leoluca/Developer/IdeaProjects/ProgettoLFT/src/test.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
