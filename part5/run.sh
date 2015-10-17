#!/bin/bash

if ! make; then exit 500; fi

OUT=/tmp/e2c.out
FAILURES=0

function run1
{
	f=$1
	bname=`basename $f`
	stub=${bname%.*}
	echo ==== $stub ====
	echo "running e2c" > $OUT
	java e2c < $stub.e > $stub.c 2>> $OUT \
	&& (echo 'compiling the C generated code' >> $OUT) && cc $stub.c \
	&& (echo 'executing the a.out' >> $OUT) && ./a.out >> $OUT
	diff "$stub".correct $OUT
	if (( $? )); then
		FAILURES=$(( $FAILURES + 1 ))
		exit 1
	fi
}

if (( $# )); then
	for f in $@; do
		run1 $f
	done
else
	for f in *.e; do
		run1 $f
	done
fi

echo DONE
if (( $FAILURES )); then
	echo $FAILURES FAILURES
else
	echo AOK!
fi
