#!/bin/bash
$(touch Recursion_times.dat)
for i in $(seq 3 15)
do
	echo "Doing Seq $i" >> Recursion_times.dat
	/usr/bin/time -a -o Recursion_times.dat -p java Recursion $i > /dev/null
	echo "Done doing $i" >> Recursion_times.dat
done
