# Test iteration statements' error for lack of a statement as first element.

@a
a=10

# A malformed block: the first element should be a statement but is a declaration.
# The parser will read it as an assignment and then choke because it thinks '@' is a variable (which it cannot find in the symbol table).
{ @w !a a = a + 1  # should cause a fatal error

	!a

}
