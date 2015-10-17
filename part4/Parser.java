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
		if (tok.kind != TK.EOF)
			parse_error("junk after logical end of program");
	}

	private void program() {
		System.out.println("#include <stdio.h>");
		System.out.println("main ()");
		block();
	}

	private void block() {
		System.out.println("{");
		declaration_list();
		statement_list();
		System.out.println("}");
	}

	private void declaration_list() {
		// below checks whether tok is in first set of declaration.
		// here, that's easy since there's only one token kind in the set.
		// in other places, though, there might be more.
		// so, you might want to write a general function to handle that.
		while (is(TK.DECLARE)) {
			declaration();
		}
	}

	private void declaration() {
		System.out.print("int ");
		mustbe(TK.DECLARE);
		Symbol symbol = symbolTable.declare(tok);
		if (symbol != null)
			System.out.print(symbol.mangle());
		mustbe(TK.ID);
		while (is(TK.COMMA)) {
			scan();
			Symbol prevSymbol = symbol;
			symbol = symbolTable.declare(tok);
			if (symbol != null) {
				if (prevSymbol != null)
					System.out.print(',');
				System.out.print(symbol.mangle());
			}
			mustbe(TK.ID);
		}
		System.out.println(";");
	}

	private void statement_list() {
		if (tok.kind == TK.EOF)
			return;
		else
			while (TK.TILDE == tok.kind || TK.ID == tok.kind || TK.PRINT == tok.kind || TK.IF == tok.kind
					|| TK.DO == tok.kind)
				statement();
	}

	private void statement() {
		if (tok.kind == TK.IF)
			if_token();
		else if (tok.kind == TK.DO)
			do_token();
		else if (tok.kind == TK.PRINT)
			print();
		else
			assignment();
	}

	private void assignment() {
		ref_id();
		mustbe(TK.ASSIGN);
		System.out.print(" = ");
		expr();
		System.out.println(';');
	}

	private void ref_id() {
		Symbol symbol = null;
		if (tok.kind == TK.TILDE) {
			scan();
			int scopeDepth = -1;
			if (tok.kind == TK.NUM)
				scopeDepth = number();
			symbol = symbolTable.get(tok, scopeDepth);
		} else
			symbol = symbolTable.get(tok);
		identifier();
		
		System.out.print(symbol.mangle());
	}

	private void identifier() {
		mustbe(TK.ID);
	}

	private void print() {
		mustbe(TK.PRINT);
		System.out.print("printf( \"%d\\n\", ");
		expr();
		System.out.println(");");
	}

	private void do_token() {
		mustbe(TK.DO);
		System.out.print("while");
		symbolTable.push();
		guarded_command();
		mustbe(TK.ENDDO); // '>'
		symbolTable.pop();
	}

	private void if_token() {
		mustbe(TK.IF);
		System.out.print("if");
		symbolTable.push();
		guarded_command();
		while (tok.kind == TK.ELSEIF) { // '|'
			System.out.print("else if");
			scan(); // '|'
			guarded_command();
		}
		if (tok.kind == TK.ELSE) { // '%'
			System.out.print("else");
			scan();
			block();
		}
		mustbe(TK.ENDIF); // ']'
		symbolTable.pop();
	}

	private void expr() {
		term();
		while (tok.kind == TK.PLUS || tok.kind == TK.MINUS) {
			System.out.print(tok.kind == TK.PLUS ? '+' : '-');
			scan();
			term();
		}
	}

	private void term() {
		factor();
		while (tok.kind == TK.TIMES || tok.kind == TK.DIVIDE) {
			System.out.print(tok.kind == TK.TIMES ? '*' : '/');
			scan();
			factor();
		}
	}

	private void factor() {
		if (tok.kind == TK.LPAREN) {
			System.out.print(" ( ");
			scan();
			expr();
			mustbe(TK.RPAREN);
			System.out.print(" ) ");
		} else if (tok.kind == TK.NUM)
			System.out.printf(" %d ", number());
		else
			ref_id();
	}

	private int number() {
		Token tok = this.tok;
		mustbe(TK.NUM);
		return Integer.parseInt(tok.string);
	}

	private void guarded_command() {
		System.out.print(" ( ");
		expr();
		System.out.println(" <= 0 )");
		mustbe(TK.THEN);
		block();
	}

	// is current token what we want?
	private boolean is(TK tk) {
		return tk == tok.kind;
	}

	// ensure current token is tk and skip over it.
	private void mustbe(TK tk) {
		if (tok.kind != tk) {
			System.err.println("mustbe: want " + tk + ", got " + tok);
			parse_error("missing token (mustbe)");
		}
		scan();
	}

	private void parse_error(String msg) {
		System.err.println("can't parse: line " + tok.lineNumber + " " + msg);
		System.exit(1);
	}

}
