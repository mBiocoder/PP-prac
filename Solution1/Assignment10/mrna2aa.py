#!/usr/bin/env python3

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
headers = []

#for infile in args.fasta:

for line in args.fasta:#infile:

    line = line.strip()

    if line.startswith(">"):

        recordName = line.split()[0][1:]

        if not recordName in headers:
            headers.append(recordName)

    else:
        head2seq[recordName] += line


# translationsmethode:
# uebersetzt einen RNA Strang in eine Proteinsequenz (Rueckgabe)
def translation(rna_seq):

    #print(rna_seq)

    # ueberpruefen ob die Laenge durch 3 teilbar ist
    if len(rna_seq) % 3 == 0 and rna_seq[0:3] == "AUG":

        #auszugebende Proteinsequenz
        prot_seq = None

        # boolean, der angibt ob ein Startcodon vorhanden ist
        start = False

        # loop ueber alle Codons
        i = 0
        while i < len(rna_seq):

            # Codondefinition
            codon = rna_seq[i:i + 3]


            # ueberpruefen ob das Codon einem Startcodon entspricht
            if codon == "AUG" and start == False:
                start = True
                prot_seq = ""
                #print("Startcodon an Position " + str(i) + " gefunden")

            # ueberpruefen ob das Codon einem Stopp Codon entspricht
            if codon == "UAA" or codon == "UAG" or codon == "UGA":
                #print("Stoppcodon an Position " + str(i) + " gefunden")
                return prot_seq

            # wenn bereits ein Startcodon vorkam wird mit der Proteinsynthese begonnen:
            if start:
                # genetischer Code:
                if codon == "AUG":
                    prot_seq += "M"
                elif codon == "UGG":
                    prot_seq += "W"
                elif codon == "UAU" or codon == "UAC":
                    prot_seq += "Y"
                elif codon == "UUU" or codon == "UUC":
                    prot_seq += "F"
                elif codon == "UGU" or codon == "UGC":
                    prot_seq += "C"
                elif codon == "AAU" or codon == "AAC":
                    prot_seq += "N"
                elif codon == "GAU" or codon == "GAC":
                    prot_seq += "D"
                elif codon == "CAA" or codon == "CAG":
                    prot_seq += "Q"
                elif codon == "GAA" or codon == "GAG":
                    prot_seq += "E"
                elif codon == "CAU" or codon == "CAC":
                    prot_seq += "H"
                elif codon == "AAA" or codon == "AAG":
                    prot_seq += "K"
                elif codon == "AUU" or codon == "AUC" or codon == "AUA":
                    prot_seq += "I"
                elif codon == "GGU" or codon == "GGC" or codon == "GGA" or codon == "GGG":
                    prot_seq += "G"
                elif codon == "GCU" or codon == "GCC" or codon == "GCA" or codon == "GCG":
                    prot_seq += "A"
                elif codon == "GUU" or codon == "GUC" or codon == "GUA" or codon == "GUG":
                    prot_seq += "V"
                elif codon == "ACU" or codon == "ACC" or codon == "ACA" or codon == "ACG":
                    prot_seq += "T"
                elif codon == "CCU" or codon == "CCC" or codon == "CCA" or codon == "CCG":
                    prot_seq += "P"
                elif codon == "CUU" or codon == "CUC" or codon == "CUA" or codon == "CUG" or codon == "UUG" or codon == "UUA":
                    prot_seq += "L"
                elif codon == "UCU" or codon == "UCC" or codon == "UCG" or codon == "AGU" or codon == "AGC" or codon == "UCA":
                    prot_seq += "S"
                elif codon == "CGU" or codon == "CGC" or codon == "CGA" or codon == "CGG" or codon == "AGA" or codon == "AGG":
                    prot_seq += "R"
                else:
                    print("Codon " + codon + " konnte nicht zugeordnet werden")

            i += 3

    else:
        #print("prot seq nicht durch 3 teilbar aber durch " + str(len(rna_seq) % 3))
        return None

# ausgabe aller header gefolgt von der jeweiligen Proteinsequenz
for i in headers:
    translation(head2seq[i])
    if not translation(head2seq[i]) == None:
        print(">{name}\n{seq}".format(name=i, seq=translation(head2seq[i])), file=args.output)
