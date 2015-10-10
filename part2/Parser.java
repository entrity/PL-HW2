/* *** This file is given as part of the programming assignment. *** */

public class Parser {


	// tok is global to all these parsing methods;
	// scan just calls the scanner's scan method and saves the result in tok.
	private Token tok; // the current token
	private void scan() {
		tok = scanner.scan();
	}

	private Scan scanner;
	Parser(Scan scanner) {
		this.scanner = scanner;
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
		mustbe(TK.ID);
		while( is(TK.COMMA) ) {
			scan();
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
			scan();
			if ( tok.kind == TK.NUM )
				number();
		}
		identifier();
	}

	private void identifier() {
		mustbe(TK.ID);
	}

	private void print() {
		mustbe(TK.PRINT);
		expr();
	}

	private void do_token() {
		mustbe(TK.DO);
		guarded_command();
		mustbe(TK.ENDDO); // '>'
	}

	private void if_token() {
		mustbe(TK.IF);
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

	private void number() {
		mustbe(TK.NUM);
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
		if( tok.kind != tk ) {
			System.err.println( "mustbe: want " + tk + ", got " +
				tok);
			parse_error( "missing token (mustbe)" );
		}
		scan();
	}

	private void parse_error(String msg) {
		System.err.println( "can't parse: line "
			+ tok.lineNumber + " " + msg );
		System.exit(1);
	}

}
