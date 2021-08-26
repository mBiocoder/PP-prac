#!/usr/bin/env python3

import argparse as ap
import matplotlib.pyplot as py
import numpy as np
from collections import defaultdict

#parse command line arguments

parser = ap.ArgumentParser()
parser.add_argument("--fasta", type = ap.FileType('r'), required = True)
parser.add_argument("--k", type = int, required = True, nargs='+')
parser.add_argument("--start", type = int, required = False)
args = parser.parse_args()

#assign content to vars
fasta = args.fasta
k_input = args.k
start = args.start

list_id = []
list_strand = []
s = ""
counter = 1
input_dict = {}

#read fasta file and save content
for line in fasta:
    if (line.startswith(">")):
        if (s != ""):
            list_strand.append(s)
            s = ""
        id = "G" + str(counter)
        counter += 1
        list_id.append(id)
    else:
        s += line.strip("\n")
list_strand.append(s)

for i in range(len(list_id)):
    input_dict[list_id[i]] = list_strand[i]

# Function that finds unique substrings of length k and returns the results in a corresponding dict format
def extractpattern(input, k_list):
    dict_out = {}
    for k in k_list:
        dict_seq = defaultdict(set)
        for id, seq in input.items():
            if not start == None:
                i = start
            else:
                i = 0
            for i in range(i,i+k+1):
                    k_mer = seq[i:i+k]
                    dict_seq[k_mer].add(id)
                    i += 1
        counter = 0
        for v in dict_seq.values():
            if len(v) == 1:
                counter += 1
        dict_out[str(k)] = counter
    return dict_out

dict_t = extractpattern(input_dict, k_input)

#process data for plotting and print results
list_y = []
list_x = []
sum = sum(dict_t.values())

#print out results
for key, value in dict_t.items():
    print(key, value, sep="\t")
    #convert to relative measure
    list_y.append(str("{:.2f}".format(value/sum)))
    list_x.append(key)

#plotting
#list_x = ["3","5","7","10","20","50"]
#list_y = [1,1,1223,2,123,5021]

#prepare plot and display
y_pos = np.arange(len(list_x))
py.bar(y_pos, list_y, color="grey")
py.xticks(y_pos, list_x)
py.title('Unique Gene Count by Pattern Length')
py.xlabel('Length k')
py.ylabel('Relative Frequencies (Percentage)')

py.show()

