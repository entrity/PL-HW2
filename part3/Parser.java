/* *** This file is given as part of the programming assignment. *** */

public class Parser {


	// tok is global to all these parsing methods;
	// scan just calls the scanner's scan method and saves the result in tok.
	private Token tok; // the current token
	private void scan() {
		tok = scanner.scan();
	}
	private Scan scanner;
	private SymbolTable symbolTable;
	
	Parser(Scan scanner) {
		this.scanner = scanner;
		this.symbolTable = new SymbolTable();
		scan();
		program();
		if( tok.kind != TK.EOF )
			parse_error("junk after logical end of program");
	}

	private void program() {
		block();
	}

	private void block(){
		declaration_list();
		statement_list();
	}

	private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
		while( is(TK.DECLARE) ) {
			declaration();
		}
	}

	private void declaration() {
		mustbe(TK.DECLARE);
		symbolTable.declare(tok);
		mustbe(TK.ID);
		while( is(TK.COMMA) ) {
			scan();
			symbolTable.declare(tok);
			mustbe(TK.ID);
		}
	}

	private void statement_list() {
		if( tok.kind == TK.EOF )
			return;
		else
			while( TK.TILDE == tok.kind || TK.ID == tok.kind || TK.PRINT == tok.kind || TK.IF == tok.kind || TK.DO == tok.kind )
				statement();
	}

	private void statement() {
		if ( tok.kind == TK.IF )
			if_token();
		else if ( tok.kind == TK.DO )
			do_token();
		else if ( tok.kind == TK.PRINT )
			print();
		else
			assignment();
	}

	private void assignment() {
		ref_id();
		mustbe(TK.ASSIGN);
		expr();
	}

	private void ref_id() {
		if ( tok.kind == TK.TILDE ) {
			int scopeDepth = 0;
			scan();
			if ( tok.kind == TK.NUM )
				scopeDepth = number();
			identifier(scopeDepth);
		}
		else
			identifier();
	}

	private void identifier() {
		symbolTable.get(tok);
		mustbe(TK.ID);
	}
	
	private void identifier(int scopeDepth) {
		symbolTable.get(tok, scopeDepth);
		mustbe(TK.ID);
	}

	private void print() {
		mustbe(TK.PRINT);
		expr();
	}

	private void do_token() {
		mustbe(TK.DO);
		symbolTable.push();
		guarded_command();
		mustbe(TK.ENDDO); // '>'
		symbolTable.pop();
	}

	private void if_token() {
		mustbe(TK.IF);
		symbolTable.push();
		guarded_command();
		while ( tok.kind == TK.ELSEIF ) { // '|'
			scan(); // '|'
			guarded_command();
		}
		if ( tok.kind == TK.ELSE ) { // '%'
			scan();
			block();
		}
		mustbe(TK.ENDIF); // ']'
		symbolTable.pop();
	}

	private void expr() {
		term();
		while ( tok.kind == TK.PLUS || tok.kind == TK.MINUS ) {
			scan();
			term();
		}
	}

	private void term() {
		factor();
		while ( tok.kind == TK.TIMES || tok.kind == TK.DIVIDE ) {
			scan();
			factor();
		}
	}

	private void factor() {
		if (tok.kind == TK.LPAREN ) {
			scan();
			expr();
			mustbe(TK.RPAREN);
		}
		else if (tok.kind == TK.NUM)
			number();
		else
			ref_id();
	}

	private int number() {
		Token tok = this.tok;
		mustbe(TK.NUM);
		return Integer.parseInt(tok.string);
	}

	private void guarded_command() {
		expr();
		mustbe(TK.THEN);
		block();
	}

	// is current token what we want?
	private boolean is(TK tk) {
		return tk == tok.kind;
	}

	// ensure current token is tk and skip over it.
	private void mustbe(TK tk) {
//		System.out.println("foo0");
		if( tok.kind != tk ) {
//			System.out.println("foo");
			System.err.println( "mustbe: want " + tk + ", got " +
				tok);
//			System.out.println("foo2");
			parse_error( "missing token (mustbe)" );
//			System.out.println("foo3");
		}
		scan();
	}

	private void parse_error(String msg) {
		System.err.println( "can't parse: line "
			+ tok.lineNumber + " " + msg );
		System.exit(1);
	}

}
