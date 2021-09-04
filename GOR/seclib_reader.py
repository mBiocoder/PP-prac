#!/usr/bin/env python3

import argparse as ap
import sys, os
import urllib
import math
from urllib.request import urlopen
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.image as mpimg

from collections import defaultdict

parser = ap.ArgumentParser()
parser.add_argument('--id', type=str, required=False)
parser.add_argument('--input', type=ap.FileType('r'), required=False)
parser.add_argument('--output', type=str, required=False, default=sys.stdout)
args = parser.parse_args()

aa_dict = {"ALA": "A", "ARG": "R", "ASN": "N", "ASP": "D", "CYS": "C", "GLN": "Q", "GLU": "E", "GLY": "G", "HIS": "H", "TYR": "Y",
           "ILE": "I", "LEU": "L", "LYS": "K", "MET": "M", "PHE": "F", "PRO": "P", "SER": "S", "THR": "T", "TRP": "W", "VAL": "V"}

rows = []

if args.id != None:
    seqid = args.id
    response = urlopen("https://files.rcsb.org/view/{}.pdb".format(seqid))
    text = response.read().decode('utf-8', 'ignore')  # read content and ignore decode errors
    rows = text.split("\n")
else:
    for row in args.input:
        rows.append(row)

atom = []
sheet = []
helix = []

aa_seqs = []
aa_seq = ""
last_aa_nr = -2
min_aa_nr = 10000
mins = []
last_aa_chain = "A"
aa_chains = []

for row in rows:

    #row = row.strip()

    # ENDMDL beendet das erste Model
    if "ENDMDL" in row:
        break

    if row.startswith("ATOM"):

        #atom_row = []
        #atom_row.append("ATOM")                             # Zeilentitel
        #atom_row.append(row[6:11].strip())                  # Atom Serien Nummer
        #atom_row.append(row[12:16].strip())                 # Atom Name
        #atom_row.append(row[16].strip())
        #atom_row.append(aa_dict.get(row[17:20].strip()))    # Aminosaeure Name
        aa = aa_dict.get(row[17:20].strip())
        #print(aa)

        #atom_row.append(row[21].strip())                    # Chain (A, B, C)
        aa_chain = row[21].strip()

        #atom_row.append(row[22:26].strip())                 # AS Nummer
        aa_nr = int(row[22:26].strip())

        if aa_chain != last_aa_chain and not aa_seq == "":
            aa_seqs.append(aa_seq)
            #print(aa_seq)
            aa_seq = ""
            aa_chains.append(last_aa_chain)
            last_aa_chain = aa_chain
            #print(aa_chain)
            mins.append(min_aa_nr)

        elif aa_nr != last_aa_nr:
            last_aa_nr = aa_nr
            aa_seq += aa

        if aa_nr < min_aa_nr:
            min_aa_nr = aa_nr





        #atom_row.append(row[26].strip())
        #atom_row.append(row[30:38].strip())                 # x
        #atom_row.append(row[38:46].strip())                 # y
        #atom_row.append(row[46:54].strip())                 # z
        #atom_row.append(row[54:60].strip())
        #atom_row.append(row[60:66].strip())
        #atom_row.append(row[76:78].strip())
        #atom_row.append(row[78:80].strip())



    elif row.startswith("SHEET"):
        # print(row)

        atom_row = []
        #atom_row.append("SHEET")
        #atom_row.append(row[7:11].strip())      # Sheet nummerierung
        #atom_row.append(row[11:14].strip())     # Sheet identifier
        #atom_row.append(row[14:16].strip())
        atom_row.append(aa_dict.get(row[17:20].strip()))     # Start AS


        atom_row.append(row[21].strip())        # Strang der AS
        atom_row.append(row[22:26].strip())     # Start Position der AS
        #atom_row.append(row[26].strip())
        atom_row.append(aa_dict.get(row[28:31].strip()))     # End AS
        atom_row.append(row[32].strip())        # Strang End AS
        atom_row.append(row[33:37].strip())     # Position End AS
        #atom_row.append(row[37].strip())
        #atom_row.append(row[38:40].strip())
        #atom_row.append(row[41:45].strip())
        #atom_row.append(row[45:48].strip())
        #atom_row.append(row[49].strip())
        #atom_row.append(row[50:54].strip())
        #atom_row.append(row[54].strip())
        #atom_row.append(row[56:60].strip())
        #atom_row.append(row[60:63].strip())
        #atom_row.append(row[64].strip())
        #atom_row.append(row[65:69].strip())
        #atom_row.append(row[69].strip())

        #print(atom_row)

        sheet.append(atom_row)



    elif row.startswith("HELIX"):

        atom_row = []
        #atom_row.append("HELIX")
        #atom_row.append(row[7:10].strip())
        #atom_row.append(row[11:14].strip())
        atom_row.append(aa_dict.get(row[15:18].strip()))     #start AS
        atom_row.append(row[19].strip())        # Strag start
        atom_row.append(row[21:25].strip())     # position start
        #atom_row.append(row[25].strip())
        atom_row.append(aa_dict.get(row[27:30].strip()))     # ende AS
        atom_row.append(row[31].strip())        # Strang ende
        atom_row.append(row[33:37].strip())     # position ende
        #atom_row.append(row[37].strip())
        #atom_row.append(row[38:40].strip())
        #atom_row.append(row[40:70].strip())
        #atom_row.append(row[71:76].strip())

        helix.append(atom_row)

aa_seqs.append(aa_seq)
#print(aa_seq)
aa_seq = ""
aa_chains.append(last_aa_chain)
last_aa_chain = aa_chain
mins.append(min_aa_nr)
#print(aa_seqs)
#print(aa_chains)
#print(mins)
aa_sec_structures = []

for seq in aa_seqs:
    ss =""
    for s in seq:
        ss += "C"
    aa_sec_structures.append(ss)

#print(aa_sec_structures)

#print(sheet)
#print(helix)

for abcd in range(len(aa_chains)):

    for hel in helix:

        if hel[1] == aa_chains[abcd]:

            i = (int(hel[2])-int(mins[abcd]))
            while i < int(hel[5])-int(mins[abcd]):
                #print(aa_seqs[abcd])
                #print(aa_sec_structures[abcd])
                #print(i)
                #print(aa_sec_structures[abcd][i])
                substring = aa_sec_structures[abcd][:i]
                #print(substring)
                substring += aa_sec_structures[abcd][i].replace("C", "H")
                if not i == len(aa_sec_structures[abcd])-1:
                    substring += aa_sec_structures[abcd][i+1:]
                aa_sec_structures[abcd] = substring
                #print(aa_sec_structures[abcd])
                i += 1

for abcd in range(len(aa_chains)):

    for she in sheet:

        if she[1] == aa_chains[abcd]:

            i = int(she[2])-2#int(min_aa_nr[abcd])
            while i < int(she[5])-2:#int(min_aa_nr[abcd]):
                #print(aa_seqs[abcd])
                #print(aa_sec_structures[abcd])
                #print(i)
                #print(aa_sec_structures[abcd][i])
                substring = aa_sec_structures[abcd][:i]
                #print(substring)
                substring += aa_sec_structures[abcd][i].replace("C", "E")
                if not i == len(aa_sec_structures[abcd])-1:
                    substring += aa_sec_structures[abcd][i+1:]
                aa_sec_structures[abcd] = substring
                #print(aa_sec_structures[abcd])
                i += 1

output_string = ""
j = 0
while j < len(aa_chains):
    output_string += ">"
    output_string += seqid
    output_string += ", "
    output_string += aa_chains[j]
    output_string += "\n"
    output_string += "AS "
    output_string += aa_seqs[j]
    output_string += "\n"
    output_string += "SS "
    output_string += aa_sec_structures[j]
    output_string += "\n"


    j += 1

print(output_string, file=args.output)