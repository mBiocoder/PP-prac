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

path_to_pdb_files = args.input
path_to_fasta_file = args.output

for (root,dirs,files) in os.walk(path_to_pdb_files):

    i = 0
    for file in files:

        if str(file).endswith(".pdb"):

            root_string = str(root).replace("\\", "/")
            root_string += "/"
            root_string += str(file)


            os.system("seclib_reader.py --input {infile} --output {outfile}".format(infile=args.input+"\\"+file, outfile=args.output)) #.format(in=file, out=args.output))
            i += 1
            print(str(i))

