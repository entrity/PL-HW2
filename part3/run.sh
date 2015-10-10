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
	echo "running e2c $stub" > $OUT 
	java e2c < $f &>> $OUT
	diff "$stub".correct $OUT
	if (( $? )); then
		FAILURES=$(( $FAILURES + 1 ))
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
