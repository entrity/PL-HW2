import java.util.LinkedList;

public class SymbolTable extends LinkedList<Scope> {

	public SymbolTable () {
		push();
	}
	/**
	 * 
	 */
	private static long serialVersionUID = 1L;
	
	public Scope pop () {
		return remove(0);
	}

	//	Actually behaves like unshift; puts new entry at position 0
	public Scope push () {
		Scope output = new Scope(serialVersionUID ++);
		add(0, output);
		return output;
	}
	
	public Symbol declare (Token tok) {
		return getFirst().declare(tok);
	}
	
	public Symbol get (Token tok) {
		for (Scope scope : this) {
			if (scope.containsKey(tok.string))
				return scope.get(tok.string);
		}
		undeclaredVariableError(tok);
		return null;
	}
	
	public Symbol get (Token tok, int depth) {
		Symbol output = null;
		Scope scope = null;
		if (depth < 0)
			scope = getLast();
		else if (this.size() > depth)
			scope = get(depth);
		if (scope != null)
			output = scope.get(tok.string);
		if (output == null) {
			if (depth < 0)
				System.err.printf("no such variable ~%s on line %d%n", tok.string, tok.lineNumber);
			else
				System.err.printf("no such variable ~%d%s on line %d%n", depth, tok.string, tok.lineNumber);
			System.exit(1);
		}
		return output;
	}
	

	private void undeclaredVariableError (Token tok) {
		System.err.printf( "%s is an undeclared variable on line %d%n", tok.string, tok.lineNumber);
		System.exit(1);
	}

}
