#!/usr/bin/python3

#imports
import urllib
from urllib.request import urlopen
import ftplib
from ftplib import FTP
import re
import argparse
import sys

#parse arguments accounting for variable length of regex
my_parser = argparse.ArgumentParser()
#my_parser.add_argument('--file', action='store')
my_parser.add_argument('--organism', action='store', nargs='+')
args = my_parser.parse_args()
#remove unwanted chars that are not part of regex
cmdargument= ' '.join(sys.argv[2:])
#print(cmdargument)


#download genome report from website and store in a text file
filename = 'prokaryotes.txt'

ftp = ftplib.FTP('ftp.ncbi.nlm.nih.gov')
ftp.login()
ftp.cwd("/genomes/GENOME_REPORTS/")

with open(filename, 'wb') as fp:
    ftp.retrbinary("RETR " + filename ,fp.write)

#make sure to have utf-8 only
f=open(filename,"r", encoding="UTF-8")
next(f) # cast into oblivion
lines=f.readlines()

#search pattern in string and print resulting values
for x in lines:
    for arg in sys.argv[2:]:
        if (re.search(arg, x.split('\t')[0])):
            print(x.split('\t')[0] + '\t' + x.split('\t')[6])
f.close()


