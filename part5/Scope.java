
import java.util.HashMap;

public class Scope extends HashMap<String, Symbol> {
	
	final long id;
	
	public Scope (long id) {
		super();
		this.id = id;
	}
	
	public Symbol declare (Token tok) {
		Symbol sym = null;
		String id = tok.string;
		if (this.get(id) != null)
			redeclarationError(tok);
		else {
			sym = new Symbol(id, this);
			this.put(id, sym);
		}
		return sym;
	}
	
	private void redeclarationError (Token tok) {
		System.err.printf("redeclaration of variable %s%n", tok.string);
	}
	
}
