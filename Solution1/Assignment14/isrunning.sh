#!/bin/bash
#Author: Mahima Arunkumar
#Date: 01.03.2021
#Modified Date: 03.03.2021
#use processname and username and check if process is running
ps axo user:20,comm | grep $1 | grep $2 > /dev/null
if [[ $? -eq 0 ]]
then
	exit 1
else
	exit 0
fi
