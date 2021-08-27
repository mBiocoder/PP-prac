#!/usr/bin/env python3

import argparse as ap
import pymysql
import sys, os

from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--fasta', type=ap.FileType('r'), default=sys.stdin, required=True)  # , nargs='+')
parser.add_argument('--db', action='store_true', required=False)
parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)
args = parser.parse_args()

"""

FASTA einlesen

"""

head2seq = defaultdict(str)

# for infile in args.fasta:

for line in args.fasta:  # infile:

    line = line.strip()

    if line.startswith(">"):

        recordName = line.split()[0][1:]

    else:
        head2seq[recordName] += line


def search_ATG(seq):
    liste = []

    z = 0
    while z <= 2:

        orf_seq = ""
        start_codon = False

        i = z
        while i < len(seq):

            codon = seq[i:i + 3]

            if codon == "ATG":  # and not start_codon:
                start_codon = True
                orf_seq += codon
                i += 3

            elif start_codon and not codon == "ATG" and not (codon == "TAA" or codon == "TAG" or codon == "TGA"):
                orf_seq += codon
                i += 3

            elif start_codon and (codon == "TAA" or codon == "TAG" or codon == "TGA"):
                orf_seq += codon
                liste.append(orf_seq)
                orf_seq = ""
                start_codon = False
                i += 3

            else:
                i += 3

        z += 1

    return liste


def reverse_strand(seq):
    seq = seq[::-1]

    rev_dict = {"A": "T", "T": "A", "C": "G", "G": "C"}

    reverse_seq = ""

    for char in seq:
        reverse_seq += rev_dict[char.upper()]

    return reverse_seq


output_string = ""

for k, v in head2seq.items():
    j = 0
    l1 = search_ATG(v)
    l2 = search_ATG(reverse_strand(v))

    while j < len(l1):
        output_string += (">" + str(k) + "_" + str(j) + "\n")
        output_string += (l1[j] + "\n")
        j += 1

    o = 0
    while o < len(l2):
        output_string += (">" + str(k) + "_" + str(o + j) + "\n")
        output_string += (l2[o] + "\n")
        o += 1

print(output_string, file=args.output)

"""
DB
"""

if args.db:

    connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user="biopraktE", passwd="JuM3IZy.8SC0c",
                                 database="biopraktE")

    cursor = connection.cursor()

    head2seq2 = defaultdict(str)

    output_string = output_string.split('\n')

    # for infile in args.fasta:

    for line in output_string:  # infile:

        line = line.strip()

        if line.startswith(">"):

            recordName = line.split()[0][1:]

        else:
            head2seq2[recordName] += line

    seq_ID_inc = 2
    for key, value in head2seq2.items():
        #print(len(value))
        #query = """INSERT INTO sequence(seq_id, sequence, type, organism) VALUES ("{k}", "{v}", "NS", "Hefe_Chromosom_1")""".format(
            #s=seq_ID_inc, k=key, v=value);
        seq_ID_inc += 1

        try:
            with connection.cursor() as cursor:
                sql = "INSERT INTO sequence(seq_id, sequence, type, organism) VALUES (%s, %s, %s,%s)"
                cursor.execute(sql, (key, value, 'NS', 'Hefe_Chromosom_1'))
            connection.commit()
            print("Query was executed successfully!");

        except Exception as e:
            print("Exception occured" + str(e));

    connection.close();
