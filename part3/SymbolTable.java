import java.util.Stack;

public class SymbolTable extends Stack<Scope> {

	public SymbolTable () {
		push();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Scope push () {
		return super.push(new Scope());
	}
	
	public Symbol declare (Token tok) {
		return lastElement().declare(tok);
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
		Scope selectedScope = this.get(depth);
		if (selectedScope != null) {
			output = selectedScope.get(tok.string);
			if (output == null)
				undeclaredVariableError(tok);
		}
		else {
			System.err.printf("No scope for index number %d%n", depth);
			System.exit(1);
		}
		return output;
	}
	

	private void undeclaredVariableError (Token tok) {
		System.err.printf( "%s is an undeclared variable on line %d%n", tok.string, tok.lineNumber);
		System.exit(1);
	}

}
