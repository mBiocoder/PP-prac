#!/usr/bin/env python3

import argparse as ap
import sys, os
import matplotlib.pyplot as plt
import numpy as np
import pylab
from pylab import xticks
#import plotly.express as px

from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--fasta', type=ap.FileType('r'), default=sys.stdin, required=True)  # , nargs='+')
parser.add_argument('--histogram', type=ap.FileType('w'), default=sys.stdout, required=False)
parser.add_argument('--lower', type=int, required=True)
parser.add_argument('--upper', type=int, required=True)
parser.add_argument('--bins', type=int, required=True)
args = parser.parse_args()

"""

FASTA einlesen

"""

head2seq = defaultdict(str)
headers = []

# for infile in args.fasta:

for line in args.fasta:  # infile:

    line = line.strip()

    if line.startswith(">"):

        recordName = line.split()[0][1:]

        if not recordName in headers:
            headers.append(recordName)

    else:
        head2seq[recordName] += line

counter = 0
list_length = []

for v in head2seq.values():
    if (len(v) > 227 and len(v) < 304) :#and (len(v) % 3 == 0):
        counter += 1

        list_length.append((len(v) - 3) // 3)


print(counter)


##########################################################
print(list_length)

#df = px.data.tips()
#counts, bins = np.histogram(df.total_bill, bins=range(75, 100, 5))
plt.hist(list_length, bins=range(args.lower, args.upper + 1, args.bins))
#xticks(range(75, 100))
plt.show()
