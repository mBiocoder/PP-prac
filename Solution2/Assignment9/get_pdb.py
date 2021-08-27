#!/usr/bin/env python3

import argparse as ap
import sys, os
import urllib
from urllib.request import urlopen

# argparser

parser = ap.ArgumentParser()
parser.add_argument('--id', type=str, required=True)
parser.add_argument('--output', type=str, default=sys.stdout, required=True)
parser.add_argument('--fasta', action='store_true', required=False)

args = parser.parse_args()
seqid = args.id
output = args.output

row_counter = 0

if args.fasta:
    response = urlopen("http://www.rcsb.org/fasta/entry/{}/display".format(seqid))
    text = response.read().decode('utf-8', 'ignore')  # read content and ignore decode errors

else:
    response = urlopen("https://files.rcsb.org/view/{}.pdb".format(seqid))
    text = response.read().decode('utf-8', 'ignore')  # read content and ignore decode errors

if (output == '-'):
    print(text)
else:
    with open(output + '/{}.pdb'.format(seqid), 'w', encoding='utf-8', newline="") as out_file:
        out_file.write(text)
