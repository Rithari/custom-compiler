public class Keyword extends Token {
	protected final String lexeme;
	public Keyword(Tag tag, String word) {
		super(tag);
		lexeme = word;
	}

	@Override
	public String toString() {
		return "<" + tag + ", \"" + lexeme + "\">";
	}

	public static final Keyword
			assign = new Keyword(Tag.ASSIGN, "assign"),
			to = new Keyword(Tag.TO, "to"),
			conditional = new Keyword(Tag.COND, "conditional"),
			option = new Keyword(Tag.OPTION, "option"),
			dotok = new Keyword(Tag.DO, "do"),
			elsetok = new Keyword(Tag.ELSE, "else"),
			whiletok = new Keyword(Tag.WHILE, "while"),
			begin = new Keyword(Tag.BEGIN, "begin"),
			end = new Keyword(Tag.END, "end"),
			print = new Keyword(Tag.PRINT, "print"),
			read = new Keyword(Tag.READ, "read"),
			or = new Keyword(Tag.OR, "||"),
			and = new Keyword(Tag.AND, "&&"),
			lt = new Keyword(Tag.RELOP, "<"),
			gt = new Keyword(Tag.RELOP, ">"),
			eq = new Keyword(Tag.RELOP, "=="),
			le = new Keyword(Tag.RELOP, "<="),
			ne = new Keyword(Tag.RELOP, "<>"),
			ge = new Keyword(Tag.RELOP, ">=");
}
