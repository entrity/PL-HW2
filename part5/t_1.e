# Try out two functionally identical blocks. (they differ only in whitespace)

@a

{ a = 5 a - 9 a = a + 1  # for a in range(5 through 9): print a and print b (which is 2*a) 
@b,c # test declaration in block
b = 2* a # test assignment in block
!a !b}

{ # same as previous block
a = 5
a - 9
a = a + 1 
@b,c b = 2* a !a !b
}
