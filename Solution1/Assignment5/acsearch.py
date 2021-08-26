#!/usr/bin/env python3
import argparse as ap
import urllib
from urllib.request import urlopen

#argparser
parser = ap.ArgumentParser()
parser.add_argument('--ac', required=True)
args = parser.parse_args()

seqid = args.ac

response = urlopen("https://www.uniprot.org/uniprot/{}.fasta".format(seqid)) #download content
text = response.read().decode('utf-8','ignore') #read content and ignore decode errors

text.split("\n")
print(text) #print FASTA file
