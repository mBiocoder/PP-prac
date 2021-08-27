#!/usr/bin/env python3

import argparse as ap
import sys, os

from collections import defaultdict
#from random import *
from random import choices

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--length', type=int, default=sys.stdin, required=True)  # , nargs='+')
parser.add_argument('--gc', type=float,default=0.5, required=True)
parser.add_argument('--name', type=str, default="", required=False)
args = parser.parse_args()



l = args.length
gc_content = args.gc
gc_int = int(gc_content * l)
at_content = 1.0 - gc_content
at_int = int(l - gc_int)

rand_seq = ""

nucletides = ["A", "T", "C", "G"]

weights = [(at_content), (at_content), (gc_content), (gc_content)]

at_sum = 0
gc_sum = 0
total_sum = 0


i = 0
while i < l:
    nuc = choices(nucletides, weights)[0]

    rand_seq += nuc

    if nuc == "A" or nuc == "T":
        at_sum += 1
    else:
        gc_sum += 1
    total_sum += 1

    i += 1

    if not total_sum == l:
        weights = [(at_int -at_sum)/(l-total_sum), (at_int -at_sum)/(l-total_sum), (gc_int -gc_sum)/(l-total_sum), (gc_int -gc_sum)/(l-total_sum)]


print(">" + args.name + "\n" + rand_seq)
