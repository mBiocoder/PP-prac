#!/bin/bash
#Author: Mahima Arunkumar
#Date: 02.03.2021
#Modified Date: 03.03.2021
#search_pid.sh
#main functtion to plot each value from outfile in gnuplot to visualize with lines

function main(){
        check $@
	for i in ${outfile}
	do
		echo $i; cat $i | gnuplot -p -e "set terminal dumb size 120, 30; set autoscale; plot '-' using 1:3 with lines notitle"
	done
}

#check function receives command line arguments and runs the process for each host.
#If the host is running the process as the given user, then its exit value is 1.
#For this host we connect via ssh and cat the contents of the relevant file in /tmp/,
#rotate 13 times to obtain the URL. Using "curl" command the content of the website
#is obtained as output data, which is then plotted using gnuplot to get the final figure. 

function check(){
        local OPTIND opt i
        while getopts ":h:u:p:o:" opt
        do
                case $opt in
                        h) hosts="$OPTARG";;
                        u) username="$OPTARG";;
                        p) process="$OPTARG";;
                        o) outfile="$OPTARG";;
                        \?) help;;
                esac
        done
	for node in $(cat $(echo ${hosts}))
	do
		#ssh ${node} "~/work/Blockteil/isrunning.sh ${process} ${username}"
		ssh ${node} "/mnt/biocluster/praktikum/bioprakt/progprakt-e/Solution1/Assignment14/isrunning.sh ${process} ${username}"
		if [[ $? == 1 ]]
		then
			thehost=${node}
		fi
	done
	curl --silent -o ${outfile} $(ssh ${thehost} "cat /tmp/ppurld.out | grep -v '^#'"|tr '[a-z]' '[n-za-m]')
}

function help(){
        echo "Wrong Options. Try again carefully!"
}

main $@
