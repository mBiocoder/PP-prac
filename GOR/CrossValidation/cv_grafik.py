#!/usr/bin/env python3

import argparse as ap
import pymysql
import sys, os
from collections import defaultdict
import matplotlib.pyplot as plt
import numpy as np

parser = ap.ArgumentParser()
parser.add_argument('--input', type=ap.FileType('r'), required=True)

# 'C:/Users/SomeUser/OneDrive/Desktop/Blatt2/Aufgabe14/2019_Feb'
args = parser.parse_args()

#inp = "SOV	gor1	fold 0	57.34\nQ3	gor1	fold 0	61.74\nSOV	gor1	fold 1	48.91\nQ3	gor1	fold 1	59.82\nSOV	gor1	fold 2	47.5\nQ3	gor1	fold 2	59.42\nSOV	gor1	fold 3	46.92\nQ3	gor1	fold 3	59.38\nSOV	gor1	fold 4	46.83\nQ3	gor1	fold 4	59.41\nSOV	gor1	mean  	49.5\nQ3	gor1	mean  	59.95\nSOV	gor3	fold 0	51.8\nQ3	gor3	fold 0	61.66\nSOV	gor3	fold 1	48.35\nQ3	gor3	fold 1	60.06\nSOV	gor3	fold 2	47.1\nQ3	gor3	fold 2	59.55\nSOV	gor3	fold 3	46.96\nQ3	gor3	fold 3	59.6\nSOV	gor3	fold 4	46.91\nQ3	gor3	fold 4	59.54\nSOV	gor3	mean  	48.22\nQ3	gor3	mean  	60.08\nSOV	gor4	fold 0	46.13\nQ3	gor4	fold 0	59.0\nSOV	gor4	fold 1	46.09\nQ3	gor4	fold 1	58.98\nSOV	gor4	fold 2	45.5\nQ3	gor4	fold 2	58.93\nSOV	gor4	fold 3	45.89\nQ3	gor4	fold 3	59.14\nSOV	gor4	fold 4	46.15\nQ3	gor4	fold 4	59.17\nSOV	gor4	mean  	45.95\nQ3	gor4	mean  	59.04"


#q3 = {"gor1": [], "gor3": [], "gor4": []}
#sov = {"gor1": [], "gor3": [], "gor4": []}

sov_fold1 = []
sov_fold2 = []
sov_fold3 = []
sov_fold4 = []
sov_fold0 = []
sov_mean = []

q3_fold1 = []
q3_fold2 = []
q3_fold3 = []
q3_fold4 = []
q3_fold0 = []
q3_mean = []



for line in args.input:

    #print(a)
    line_array = line.split("\t")

    if line_array[0].strip() == "SOV":

        if line_array[2].strip() == "fold 1":
            sov_fold1.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 0":
            sov_fold0.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 2":
            sov_fold2.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 3":
            sov_fold3.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 4":
            sov_fold4.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "mean":
            sov_mean.append(float(line_array[3].strip()))

    elif line_array[0].strip() == "Q3":

        if line_array[2].strip() == "fold 1":
            q3_fold1.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 0":
            q3_fold0.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 2":
            q3_fold2.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 3":
            q3_fold3.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "fold 4":
            q3_fold4.append(float(line_array[3].strip()))
        elif line_array[2].strip() == "mean":
            q3_mean.append(float(line_array[3].strip()))




barWidth = (1/8)
fig, ax = plt.subplots(figsize=(12, 8))
br1 = np.arange(len(q3_fold4))
br2 = [x + barWidth for x in br1]
br3 = [x + barWidth for x in br2]
br4 = [x + barWidth for x in br3]
br5 = [x + barWidth for x in br4]
br6 = [x + barWidth for x in br5]

# Make the plot
plt.bar(br1, q3_fold0, color='#900C3F', width=barWidth,
        edgecolor='grey', label='Q3 Fold 0')
plt.bar(br2, q3_fold1, color='#C70039', width=barWidth,
        edgecolor='grey', label='Q3 Fold 1')
plt.bar(br3, q3_fold2, color='#FF5733', width=barWidth,
        edgecolor='grey', label='Q3 Fold 2')
plt.bar(br4, q3_fold3, color='#FFC300', width=barWidth,
        edgecolor='grey', label='Q3 Fold 3')
plt.bar(br5, q3_fold4, color='#FFFFA1', width=barWidth,
        edgecolor='grey', label='Q3 Fold 4')
plt.bar(br6, q3_mean, color='grey', width=barWidth,
        edgecolor='grey', label='Q3 Mean')


# Adding Xticks
plt.xlabel('GOR Version', fontweight='bold', fontsize=15)
plt.ylabel('Performance', fontweight='bold', fontsize=15)
plt.xticks([r + barWidth for r in range(len(q3_fold4))],
           ['GOR1', 'GOR3', 'GOR4'])
#ax.set_xticklabels(['GOR1', 'GOR3', 'GOR4'])

plt.title("Q3")

#ax.set_xticklabels(["F0", "F1", "F2", "F3", "F4", "mean"])

plt.legend()
plt.savefig('C:/Users/SomeUser/OneDrive/Desktop/q3.png')


###############################################################################
### SOV ############

barWidth = (1/8)
fig2, ax22 = plt.subplots(figsize=(12, 8))
br1 = np.arange(len(sov_fold4))
br2 = [x + barWidth for x in br1]
br3 = [x + barWidth for x in br2]
br4 = [x + barWidth for x in br3]
br5 = [x + barWidth for x in br4]
br6 = [x + barWidth for x in br5]

# Make the plot
plt.bar(br1, sov_fold0, color='#900C3F', width=barWidth,
        edgecolor='grey', label='SOV Fold 0')
plt.bar(br2, sov_fold1, color='#C70039', width=barWidth,
        edgecolor='grey', label='SOV Fold 1')
plt.bar(br3, sov_fold2, color='#FF5733', width=barWidth,
        edgecolor='grey', label='SOV Fold 2')
plt.bar(br4, sov_fold3, color='#FFC300', width=barWidth,
        edgecolor='grey', label='SOV Fold 3')
plt.bar(br5, sov_fold4, color='#FFFFA1', width=barWidth,
        edgecolor='grey', label='SOV Fold 4')
plt.bar(br6, sov_mean, color='grey', width=barWidth,
        edgecolor='grey', label='SOV Mean')


# Adding Xticks
plt.xlabel('GOR Version', fontweight='bold', fontsize=15)
plt.ylabel('Performance', fontweight='bold', fontsize=15)
plt.xticks([r + barWidth for r in range(len(q3_fold4))],
           ['GOR1', 'GOR3', 'GOR4'])

plt.title("SOV")

#ax.set_xticklabels(["F0", "F1", "F2", "F3", "F4", "mean"])

plt.legend()
plt.savefig('C:/Users/SomeUser/OneDrive/Desktop/sov.png')
