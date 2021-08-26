#!/usr/bin/env python3

# Loesung fuer Aufgabe b) 2

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



dict_nuc = {'A': 0, 'C': 0, 'G': 0, 'T': 0}

#print(dict_nuc)


for v in head2seq.values():

    #print(v)

    for nuc in v:
        #print(nuc)

        dict_nuc[nuc.upper()] += 1

#print(dict_nuc)

sum = 0
for k, v in dict_nuc.items():
    sum += int(v)

#print(sum)

xx = ["A", "C", "G", "T"]
for x in xx:
    dict_nuc[x] = dict_nuc[x] / sum

#print(dict_nuc)

def compute_seq_prob(seq):

    prob = 1.0

    for s in seq:
        prob = prob * dict_nuc[s]

    return prob




# Methode die berechnet wie haeufig ein String in einem Genom vorkommen wird
def compute_quantity(seq):

    probability = compute_seq_prob(seq)

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
    print("{s}: {c}".format(s=seq, c=compute_quantity(seq)), file=args.output)