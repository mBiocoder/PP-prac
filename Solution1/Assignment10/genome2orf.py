#!/usr/bin/env python3

# importe:
import argparse as ap
import sys, os

from collections import defaultdict


def reverse_complement(string):
    rev_string = string[::-1]

    rev_dict = {"A": "T", "T": "A", "C": "G", "G": "C"}

    result_string = ""

    for char in rev_string:
        result_string += rev_dict[char.upper()]

    return result_string

    #print(string)
    #print(rev_string)



    #return rev_string


if __name__ == '__main__':

    # argparser
    parser = ap.ArgumentParser()
    parser.add_argument('--organism', type=ap.FileType('r'), default=sys.stdin, required=True, nargs='+')
    parser.add_argument('--features', type=ap.FileType('r'), required=True, nargs='+')
    parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)

    args = parser.parse_args()

    head2seq = defaultdict(str)

    for infile in args.organism:

        for line in infile:

            line = line.strip()

            if line.startswith(">"):

                recordName = line.split()[0][1:]

            else:
                head2seq[recordName] += line

    for infile in args.features:

        for line in infile:

            line = line.strip()
            aline = line.split("\t")

            if not aline[0] == "CDS":
                continue

            # locus tag wird extrahiert fuer den Titel
            seqLocusTag = aline[16]
            #Chromosom ID wird extrahiert
            seqChr = aline[6]
            # Starpunkt
            seqStart = int(aline[7])
            # Endpunkt
            seqEnd = int(aline[8])
            # +/- Strang
            seqStrand = aline[9]

            sequence = ""

            # Sequenz des entsprechenden Chromosoms wird zwischengespeichert
            chrSeq = head2seq.get(seqChr, None)

            # Sequenz wird auf den orf zugeschnitten
            sequence = chrSeq[seqStart - 1:seqEnd]

            # wenn die Sequenz auf dem - Strang liegt wird das reverse Komplement gebildet
            if seqStrand == "-":
                sequence = reverse_complement(sequence)



            print(">{name}\n{seq}".format(name=seqLocusTag, seq=sequence), file=args.output)