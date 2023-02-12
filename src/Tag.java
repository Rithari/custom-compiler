public enum Tag {
	NUM(256),
	ID(257),
	RELOP(258),
	ASSIGN(259),
	TO(260),
	COND(261),
	OPTION(262),
	DO(263),
	ELSE(264),
	WHILE(265),
	BEGIN(266),
	END(267),
	PRINT(268),
	READ(269),
	OR(270),
	AND(271),
	NOT('!'),
	PTA('('),
	PTC(')'),
	PQA('['),
	PQC(']'),
	PGA('{'),
	PGC('}'),
	SUM('+'),
	SUB('-'),
	MUL('*'),
	DIV('/'),
	SEMICOLON(';'),
	COMMA(','),
	EOF(-1);

	private final int tagValue;

	Tag(int tagValue) {
		this.tagValue = tagValue;
	}

	public int getTag() {
		return this.tagValue;
	}


	@Override
	public String toString() {
		return "" + this.tagValue;
	}
}
