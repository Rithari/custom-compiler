public class Token {
	protected final Tag tag;
	public Token(Tag tag) {
		this.tag = tag;
	}
	public String toString() {
		return "<" + tag + ">";
	}

	public static final Token
			not = new Token(Tag.NOT),
			pta = new Token(Tag.PTA),
			ptc = new Token(Tag.PTC),
			pqa = new Token(Tag.PQA),
			pqc = new Token(Tag.PQC),
			pga = new Token(Tag.PGA),
			pgc = new Token(Tag.PGC),
			sum = new Token(Tag.SUM),
			sub = new Token(Tag.SUB),
			mul = new Token(Tag.MUL),
			div = new Token(Tag.DIV),
			semicolon = new Token(Tag.SEMICOLON),
			comma = new Token(Tag.COMMA),
			eof = new Token(Tag.EOF);

	public Tag getTag() {
		return this.tag;
	}
}