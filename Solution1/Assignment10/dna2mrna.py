#!/usr/bin/env python3

# arg parser:

# importe:
import argparse as ap
import sys, os

from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--fasta', type=ap.FileType('r'), default=sys.stdin, required=True)#, nargs='+')
parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)
args = parser.parse_args()


"""
FASTA einlesen
"""

head2seq = defaultdict(str)

#for infile in args.fasta:

for line in args.fasta:#infile:

    line = line.strip()

    if line.startswith(">"):

        recordName = line.split()[0][1:]

    else:
        head2seq[recordName] += line


####################################################################################


# 2:
# Transkription:
# DNA in RNA uebersetzen

def transcription(dna):

    rna = ''

    for nucleotide in dna:
        if nucleotide == 'A' or nucleotide == 'a':
            rna += 'A'
        elif nucleotide == 'C' or nucleotide == 'c':
            rna += 'C'
        elif nucleotide == 'T' or nucleotide == 't':
            rna += 'U' # 'A'
        elif nucleotide == 'G' or nucleotide == 'g':
            rna += 'G'
    #print(rna)
    return rna

"""

AUSGABE

"""

for k, v in head2seq.items():
    print(">{name}\n{seq}".format(name=k, seq=transcription(v)), file=args.output)
