#!/usr/bin/env python3

import argparse as ap
import pymysql
import sys, os
from collections import defaultdict

parser = ap.ArgumentParser()
parser.add_argument('--input', type=str, required=True)                         # Homstrad DB Folder
parser.add_argument('--output', type=str, default=sys.stdout, required=True)    # Output Directory for pdb Files
# --input 'C:/Users/leona/OneDrive/Desktop/Blatt2/Aufgabe14/2019_Feb' --output 'C:/Users/leona/OneDrive/Desktop/Blatt2/Aufgabe14/pdbs'
args = parser.parse_args()

pfad = args.input

seqs = []

alis = 0

for (root,dirs,files) in os.walk(pfad):

    #print(str(root).split("/")[-1])
    for file in files:

        if str(file).endswith(".ali"):

            #print(str(root))
            root_string = str(root).replace("\\", "/")
            root_string += "/"
            root_string += str(file)
            #print(root_string)
            #print(str(file))
            #alis += 1

            #family = ""

            with open(root_string, "r", encoding="UTF-8", newline="") as in_file:

                name = ""

                seqs_in_file = defaultdict(str)

                #i = 0
                for line in in_file:

                    if line.startswith(">"):
                        name = (line.split(";")[1].strip())
                        seqs.append(name)
                        break

                        #name2fam[name] = family
                        #fam2name[family].append(name)
                        #print(name)

out_string = ""
i = 0
for seq in seqs:
    out_string += seq
    out_string += "\n"
    os.system("get_pdb.py --id {id} --output {out}".format(id=seq, out=args.output))
    i += 1
    print(seq + ": " + str(i) + " von " + str(len(seqs)))
#print(out_string)
#print(len(out_string))
#print(pfad[:-8] + "pdbs")
