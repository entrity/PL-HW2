public class Token {
	public TK kind;
	public String string;
	public int lineNumber;
	public final int charNumber;
	public Token(TK kind, String string, int lineNumber, int charNumber) {
		this.kind = kind;
		this.string = string;
		this.lineNumber = lineNumber;
		this.charNumber = charNumber - string.length();
	}
	public String toString() { // make it printable for debugging
		return "Token("+kind.toString()+" "+string+" "+lineNumber+")";
	}
}
