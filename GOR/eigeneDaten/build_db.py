#!/usr/bin/env python3

import argparse as ap
import pymysql
import sys, os
from collections import defaultdict

parser = ap.ArgumentParser()
parser.add_argument('--input', type=str, required=True)
parser.add_argument('--output', type=str, default=sys.stdout, required=True)
# 'C:/Users/leona/OneDrive/Desktop/Blatt2/Aufgabe14/2019_Feb'
args = parser.parse_args()

path_to_fasta_files = args.input
path_to_output_db_file = args.output

seqs = []
output_string = ""


for (root,dirs,files) in os.walk(path_to_fasta_files):


    for file in files:

        if str(file).endswith(".fasta"):

            root_string = str(root).replace("\\", "/")
            root_string += "/"
            root_string += str(file)


            with open(root_string, "r", encoding="UTF-8", newline="") as in_file:

                i = 0
                for line in in_file:

                    if line.startswith(">") or line.startswith("AS ") or line.startswith("SS "):
                        output_string += line.strip()
                        output_string += "\n"
                    if line.startswith("SS "):
                        output_string += "\n"
                        #print(name)

print(output_string)
f = open(args.output, "w")
f.write(output_string)
f.close()