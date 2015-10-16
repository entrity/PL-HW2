public class Symbol {
	
	String id; // identifier
	Scope scope;
	// type
	// value
	
	public Symbol (String id, Scope scope) {
		this.id = id;
		this.scope = scope;
	}
	
	public String mangle () {
		return String.format(" x_%s_%d", id, scope.id );
	}
	
}