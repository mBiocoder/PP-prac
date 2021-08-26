#!/usr/bin/env python3

# Loesung fuer Aufgabe b) 2

# importe:
import argparse as ap
import sys, os
from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--sequence', type=str, default=sys.stdin, required=True, nargs='+')
parser.add_argument('--genome', type=ap.FileType('r'), default=sys.stdin, required=True, nargs='+')
parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)
parser.add_argument('--titel', type=str, default="<Organismus>", required=False, nargs='+')
args = parser.parse_args()

""""Methode, die fuer eine Sequenz die Wahscheinlichkeit berechnet"""
def compute_seq_prob(seq, dict_nuc):

    prob = 1.0

    for s in seq:
        prob = prob * dict_nuc[s]

    return prob

"""Methode die berechnet wie haeufig ein String in einem Genom vorkommen wird"""
def compute_quantity(seq, head2seq, dict_nuc):

    probability = compute_seq_prob(seq, dict_nuc)

    result = 0.0

    # es werden alle fasta Sequenzen durchlaufen
    for v in head2seq.values():

        if len(seq) <= len(v):
            a = probability * (len(v)-len(seq)+1)
            result += a

    return result


"""" Methode die berechnet wie haeufig ein String wahrscheinlich in einem Genom vorkommen wird ausgehend von p=1/4 """
def compute_equal(seq, head2seq):
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


""" Methode die die tatsaechliche inzidenz berechnet """
def count(seq, head2seq):

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

""" Methode, die den fc berechnet """
def compute_fc(seq, head2seq, dict_nuc):
    inc = count(seq, head2seq)
    compute_inc = compute_quantity(seq, head2seq, dict_nuc)
    return (inc / compute_inc)


orga_count = 0

for infile in args.genome:

    """
    FASTA einlesen
    """

    head2seq = defaultdict(str)
    # heads = []
    # seqs = []

    # for infile in args.fasta:

    for line in infile:

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

            if nuc == "A" or nuc == "C" or nuc == "G" or nuc == "T":
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


    # Ausgabe:
    output_string = str("{orga}\n\n".format(orga=args.titel[orga_count]))
    orga_count += 1
    output_string += str("A: {a_in_prozent} % C: {c_in_prozent} % G: {g_in_prozent} % T: {t_in_prozent} %\n\n".format(a_in_prozent=round(dict_nuc["A"]*100, 2),
                                                                                                 c_in_prozent=round(dict_nuc["C"]*100,2),
                                                                                                 g_in_prozent=round(dict_nuc["G"]*100, 2),
                                                                                                 t_in_prozent=round(dict_nuc["T"]*100, 2)))

    #print(output_string)

    for seq in args.sequence:
        # print(seq + ": " + str(count(seq)))
        output_string += ("{s}:\t{equal}\t{quantity}\t{inc}\t{fc}\n".format( s=seq, equal=int(compute_equal(seq, head2seq)),
                                                                             quantity=int(compute_quantity(seq, head2seq, dict_nuc)),
                                                                             inc=int(count(seq, head2seq)),
                                                                             fc=round(compute_fc(seq, head2seq, dict_nuc),1)))#, file=args.output)

    output_string += "\n"

    """Ausgabe des Ergebnis"""
    print(output_string, file=args.output)