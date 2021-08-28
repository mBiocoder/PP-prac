#!/bin/bash
$(touch CodingNW_times.dat)
for i in $(seq 3 15)
do
	echo "Doing Seq $i" >> CodingNW_times.dat
	/usr/bin/time -a -o CodingNW_times.dat -p java CodingNW $i > /dev/null
	echo "Done doing $i" >> CodingNW_times.dat
done
