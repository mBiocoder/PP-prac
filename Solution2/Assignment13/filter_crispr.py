#!/usr/bin/env python3

import argparse as ap
import sys, os
import urllib
import math
from urllib.request import urlopen

from collections import defaultdict

# argparser

parser = ap.ArgumentParser()
parser.add_argument('--sam', type=ap.FileType("r"), required=True)
parser.add_argument('--no-off-targets', '-o', type=ap.FileType("w"), required=True)
parser.add_argument('--with-mismatch', '-wm', type=ap.FileType("w"), required=True)
args = parser.parse_args()
#parser.parse_args(['-'])

response = args.sam
text = response.read().decode('utf-8', 'ignore')  # read content and ignore decode errors
rows = text.split("\n")

row_counter = 0
alignment_line = []
alignments = {}

#read alignment lines
for row in rows:
    row_counter += 1

    row = row.strip()

    if not row.startswith("@"):
        a_row = row.split(" ")

        a_row_II = []
        for a_ro in a_row:
            if not a_ro == "":
                a_row_II.append(a_ro)

        alignment_line.append(a_row_II)
    else:
        continue



"""
For example, a string ‘10A5^AC6’ means from the leftmost reference base in the alignment, there are10 matches followed by an A on the reference which is different from the aligned read base; the next 5reference bases are matches followed by a 2bp deletion from the reference; the deleted sequence is AC;the last 6 bases are matches.
"""

#dict for the specific alignments (=key) and MD's (=value)
for align in alignment_line:
    for start in align:
        if(start.startswith('MD:Z:')):
            md_string = start[5:]
            md = 0
            for stri in md_string:
                if (stri == 'A' or stri == 'G' or stri == 'C' or stri == 'T' ):
                    md += 1
            alignments[align[9]] = md #md: amount of mismatches
            
#reverse alignment von align[9] fehlt noch !!

#no Off-Target
with open(args.o, "w", encoding="UTF-8", newline="") as out_file:
        for key,value in alignments.items():
            for align in alignment_line:
                if value <= 3: #max 3 mistakes
                    out_file.write('>'+str(align[0]))
                    out_file.write(key)

#Mismatch sequence
with open(args.wm, "w", encoding="UTF-8", newline="") as out_file:
        for key,value in alignments.items():
            for align in alignment_line:
                if value <= 3 and align[9].endswith('GG'):
                    out_file.write('>' +str(align[0]))
                    out_file.write(key)
