#!/usr/bin/env python3

import argparse as ap
import subprocess

#define and parse arguments
parser = ap.ArgumentParser()
parser.add_argument('--id', type = str, required = True)
parser.add_argument('--output', type = ap.FileType('w'), required = False)
parser.add_argument('--colourized', action = 'store_true', required = False)
parser.add_argument('--html', action = 'store_true', required = False)

args = parser.parse_args()
seqid = args.id

#run subprocess to generate
sub1 = subprocess.run("python get_pdb_back.py --id %s --output C:/Users/hanna/propra/blockteil1" %seqid, shell=True)

#check whether secondary structure should be color-highlighted
if args.colourized == False:
    #runs subprocess to generate .png file with (cartoon-style) protein structure
    output = subprocess.run("java -jar ./jmol/Jmol.jar -n -s ./asst9.txt ./%s.pdb -w PNG:%s.png"
                        %(seqid, seqid), shell=True, stdout=subprocess.PIPE)
else:
    #runs subprocess with script based on .pdb annotations
    output = subprocess.run("java -jar ./jmol/Jmol.jar -n -s ./asst9_pdb_annot.txt ./%s.pdb -w PNG:%s.png" % (seqid, seqid),
                             shell=True, stdout=subprocess.PIPE)

    #Alternative: runs subprocess with script based on jmol standard settings
    #output = subprocess.run("java -jar ./jmol/Jmol.jar -n -s ./asst9_pdb_struct.txt ./%s.pdb -w PNG:%s.png" % (seqid, seqid),
    #                        shell=True, stdout=subprocess.PIPE)