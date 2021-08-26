#!/usr/bin/env python3

# Loesung fuer Aufgabe a)

# importe:
import argparse as ap
import sys, os
from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--sequence', type=str, default=sys.stdin, required=True, nargs='+')
parser.add_argument('--genome', type=ap.FileType('r'), default=sys.stdin, required=True)  # , nargs='+')
parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)
args = parser.parse_args()

"""
FASTA einlesen
"""

head2seq = defaultdict(str)
# heads = []
# seqs = []

# for infile in args.fasta:

for line in args.genome:  # infile:

    line = line.strip()

    if line.startswith(">"):

        recordName = line.split()[0][1:]

        # if not recordName in heads:
        # heads.append(recordName)

    else:
        head2seq[recordName] += line


# print(head2seq)

# Methode die berechnet wie haeufig ein String in einem Genom vorkommen wird
def compute_equal(seq):
    # da alle Nucleotide gleich wahrscheinlich sind in nur die Laenge von seq relevant
    probability = 0.25**len(seq)
    #print(probability)

    result = 0.0

    # es werden alle fasta Sequenzen durchlaufen
    for v in head2seq.values():

        if len(seq) <= len(v):
            a = probability * (len(v)-len(seq)+1)
            result += a

    return result




# Ausgabe:
for seq in args.sequence:
    # print(seq + ": " + str(count(seq)))
    print("{s}: {c}".format(s=seq, c=compute_equal(seq)), file=args.output)