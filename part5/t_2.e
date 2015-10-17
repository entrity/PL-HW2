# Try out nested iteration statements.
# Test iteration statements for declaring variables that shadow higher-scoped variables. (Blocks for iteration statements create new scopes.) Test use of the scope operator from within iteration statements.

@a,b
b=9
{ a = 0 a - 5 a = a + 1  # for a in range(0 through 5)...
	@c,b # declare a 'b' to shadow the global-scoped 'b'
	b=7
	{ c=0 c-2 c=c+1 # for c in range(0 through 2)...
		!b
		!~b
		!a
	}

}
