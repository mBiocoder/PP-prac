#!/usr/bin/env python3

# Loesung fuer Aufgabe a)

# importe:
import argparse as ap
import sys, os
from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--sequence', type=str, default=sys.stdin, required=True, nargs='+')
parser.add_argument('--genome', type=ap.FileType('r'), default=sys.stdin, required=True)#, nargs='+')
parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)
args = parser.parse_args()


"""
FASTA einlesen
"""

head2seq = {}
#heads = []
#seqs = []

#for infile in args.fasta:

for line in args.genome:#infile:

    line = line.strip()

    if line.startswith(">"):

        recordName = line.split()[0][1:]
        head2seq[recordName] = ""

        #if not recordName in heads:
            #heads.append(recordName)

    else:
        head2seq[recordName] += line

#print(head2seq)


# Methode, die als Eingabe eine Sequenz bekommt um dann fuer die Sequenz die Vorkommen zu zaehlen in der .fasta Datei
def count(seq):

    # zaehlt die Vorkommen
    counter = 0

    # alle .fasta Sequenzen werden durchlaufen
    for v in head2seq.values():

        # nur wenn die Sequenz kuerzer als das Genom ist
        if len(seq) <= len(v):

            # Durchlauf durch das gesamte Genom
            i = 0
            while i < len(v)-len(seq)+1:

                # Vergleich ob Genom Substring und Sequenz identisch sind
                if seq == v[i:i+len(seq)]:

                    # wenn ja: counter erhoehen
                    counter += 1

                i += 1

    return counter

# Ausgabe:
for seq in args.sequence:
    #print(seq + ": " + str(count(seq)))
    print("{s}: {c}".format(s=seq, c=count(seq)), file=args.output)