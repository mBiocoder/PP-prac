#!/bin/bash
#Author: Arunkumar
#Date: 01.03.2021
#find the load average and print the first value
output=$(ssh $1 "cat /proc/loadavg"|awk '{print $1}')
echo ${output}
