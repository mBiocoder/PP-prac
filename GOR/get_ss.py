#!/usr/bin/env python3

import argparse as ap
import sys, os
#import urllib
#import math
from urllib.request import urlopen
#import matplotlib.pyplot as plt
#import numpy as np
#import matplotlib.image as mpimg

from collections import defaultdict

parser = ap.ArgumentParser()
parser.add_argument('--input', type=ap.FileType('r'), required=False)
parser.add_argument('--output', type=str, required=False, default=sys.stdout)
args = parser.parse_args()

out = ""

for row in args.input:
    if row.startswith("SS "):
        out += row.replace("AS ", "")
    elif row.startswith("AS "):
        continue
    else:
        out += row

f = open(args.output, 'w')
f.write(out)
f.close()