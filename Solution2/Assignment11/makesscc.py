#!/usr/bin/env python3

# --id "4YBV" --length 3 --distance 5 --type "CE1" --output "C:/Users/someUser"

import argparse as ap
import sys, os
import urllib
import math
from urllib.request import urlopen
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.image as mpimg

from collections import defaultdict

# argparser

parser = ap.ArgumentParser()
parser.add_argument('--id', type=str, required=True)
parser.add_argument('--distance', type=int, required=True)
parser.add_argument('--type', type=str, required=True)
parser.add_argument('--length', type=int, required=True)
parser.add_argument('--output', type=str, required=False)
args = parser.parse_args()

out_string_total = "chain\tpos\tserial\taa\tss\tglobal\tlocal\n"

seqid = args.id

response = urlopen("https://files.rcsb.org/view/{}.pdb".format(seqid))
text = response.read().decode('utf-8', 'ignore')  # read content and ignore decode errors
rows = text.split("\n")
#print(rows)

#print(text)

dataset = [line.strip().split() for line in text]
#list_all = []
atom = []
sheet = []
helix = []

row_counter = 0

for row in rows:
    row_counter += 1

    row = row.strip()

    # ENDMDL beendet das erste Model
    if "ENDMDL" in row:
        break

    if row.startswith("ATOM"):

        atom_row = []
        atom_row.append("ATOM")
        atom_row.append(row[6:11].strip())
        atom_row.append(row[12:16].strip())
        #atom_row.append(row[16].strip())
        atom_row.append(row[17:20].strip())
        atom_row.append(row[21].strip())
        atom_row.append(row[22:26].strip())
        #atom_row.append(row[26].strip())
        atom_row.append(row[30:38].strip())
        atom_row.append(row[38:46].strip())
        atom_row.append(row[46:54].strip())
        #atom_row.append(row[54:60].strip())
        #atom_row.append(row[60:66].strip())
        #atom_row.append(row[76:78].strip())
        #atom_row.append(row[78:80].strip())

        if atom_row[2] == args.type:
            atom.append(atom_row)
            print(atom_row)


    elif row.startswith("SHEET"):
        # print(row)

        atom_row = []
        atom_row.append("SHEET")
        atom_row.append(row[7:11].strip())
        atom_row.append(row[11:14].strip())
        atom_row.append(row[14:16].strip())
        atom_row.append(row[17:20].strip())
        atom_row.append(row[21].strip())
        atom_row.append(row[22:26].strip())
        #atom_row.append(row[26].strip())
        atom_row.append(row[28:31].strip())
        atom_row.append(row[32].strip())
        atom_row.append(row[33:37].strip())
        #atom_row.append(row[37].strip())
        #atom_row.append(row[38:40].strip())
        #atom_row.append(row[41:45].strip())
        #atom_row.append(row[45:48].strip())
        #atom_row.append(row[49].strip())
        #atom_row.append(row[50:54].strip())
        #atom_row.append(row[54].strip())
        #atom_row.append(row[56:60].strip())
        #atom_row.append(row[60:63].strip())
        #atom_row.append(row[64].strip())
        #atom_row.append(row[65:69].strip())
        #atom_row.append(row[69].strip())

        print(atom_row)

        sheet.append(atom_row)



    elif row.startswith("HELIX"):

        atom_row = []
        atom_row.append("HELIX")
        atom_row.append(row[7:10].strip())
        atom_row.append(row[11:14].strip())
        atom_row.append(row[15:18].strip())
        atom_row.append(row[19].strip())
        atom_row.append(row[21:25].strip())
        #atom_row.append(row[25].strip())
        atom_row.append(row[27:30].strip())
        atom_row.append(row[31].strip())
        atom_row.append(row[33:37].strip())
        #atom_row.append(row[37].strip())
        #atom_row.append(row[38:40].strip())
        #atom_row.append(row[40:70].strip())
        #atom_row.append(row[71:76].strip())

        helix.append(atom_row)

aa_dict = {"ALA": "A", "ARG": "R", "ASN": "N", "ASP": "D", "CYS": "C", "GLN": "Q", "GLU": "E", "GLY": "G", "HIS": "H", "TYR": "Y",
           "ILE": "I", "LEU": "L", "LYS": "K", "MET": "M", "PHE": "F", "PRO": "P", "SER": "S", "THR": "T", "TRP": "W", "VAL": "V"}


print(atom)

output_distance = []

i = 0
while i < len(atom):

    kontakt = False

    j = 0
    while j < len(atom):

        if not i == j and not kontakt == True: # and not atom[i][4] == atom[j][4]:

            zwischenergebnis_1 = math.sqrt((float(atom[i][6])-float(atom[j][6]))**2 +
                                           (float(atom[i][7])-float(atom[j][7]))**2 +
                                           (float(atom[i][8])-float(atom[j][8]))**2 )

            print(zwischenergebnis_1)
            #z1 = math.sqrt((float(atom[i][6]))**2 + (float(atom[i][7]))**2 + (float(atom[i][8]))**2 )
            #z2 = math.sqrt((float(atom[j][6])) ** 2 + (float(atom[j][7])) ** 2 + (float(atom[j][8])) ** 2)
            #print(zwischenergebnis)


            if args.distance > zwischenergebnis_1: #abs(z1 - z2):
                kontakt = True
                output_distance.append(atom[i])
                #print(abs(z1-z2))
                #print(atom[i])

        j +=1

    i += 1

#with open(args.output+"/"+args.id+".sscc", "w", encoding="UTF-8", newline="") as outfile:

for out in output_distance:

    out_string = out[4]
    out_string += "\t"
    out_string += out[5]
    out_string += "\t"
    out_string += out[1]
    out_string += "\t"
    if out[3] in aa_dict:
        out_string += aa_dict[out[3]]
    out_string += "\t."
    """ sekundärstruktur"""

    """ helix """
    for heli in helix:
        if ( heli[4] == out[4] ) and ( heli[7] == out[4]) and ( int(heli[5]) <= int(out[5]) ) and ( int(out[5]) <= int(heli[8]) ):
            #print(heli[5] + " <= " + out[5] + " <= " + heli[8])
            out_string = out_string[:-1]
            out_string += "H"
            out_string += "\t"

    for shee in sheet:
        if ( shee[4] == out[4] ) and ( shee[7] == out[4]) and ( int(shee[5]) <= int(out[5]) ) and ( int(out[5]) <= int(shee[8]) ):
            #print(shee[5] + " <= " + out[5] + " <= " + shee[8])
            out_string = out_string[:-1]
            out_string += "E"
            out_string += "\t"

    #print(out_string.split("\t"))

    if out_string.split("\t")[4] == ".":

        out_string = out_string[:-1]
        out_string += "C"
        out_string += "\t"

    """ global """

    glob = 0

    #z1 = math.sqrt((float(out[6])) ** 2 + (float(out[7])) ** 2 + (float(out[8])) ** 2)


    for out2 in output_distance:

        #z2 = math.sqrt((float(out2[6])) ** 2 + (float(out2[7])) ** 2 + (float(out2[8])) ** 2)
        zwischenergebnis_1 = math.sqrt((float(out[6]) - float(out2[6])) ** 2 +
                                       (float(out[7]) - float(out2[7])) ** 2 +
                                       (float(out[8]) - float(out2[8])) ** 2)

        if not out[5] == out2[5] and out[4] == out2[4] and abs(int(out[5]) - int(out2[5])) >= args.length and abs(zwischenergebnis_1) < args.distance:
            glob += 1

    out_string += str(glob)

    out_string += "\t"


    """ lokal """

    local = 0

    for out2 in output_distance:

        #z2 = math.sqrt((float(out2[6])) ** 2 + (float(out2[7])) ** 2 + (float(out2[8])) ** 2)
        zwischenergebnis_1 = math.sqrt((float(out[6]) - float(out2[6])) ** 2 +
                                       (float(out[7]) - float(out2[7])) ** 2 +
                                       (float(out[8]) - float(out2[8])) ** 2)

        if  not out[5] == out2[5] and out[4] == out2[4] and abs(int(out[5]) - int(out2[5])) < args.length and abs(zwischenergebnis_1) < args.distance:
            local += 1

    out_string += str(local)



    out_string += "\n"
    out_string_total += out_string

if not args.output == None:
    filename= args.output + "/" + args.id + ".sscc"
    with open(filename, "w", encoding="UTF-8", newline="") as out_file:
        out_file.write(out_string_total)
else:
    print(out_string_total)




matrix_x = []

for out_x in output_distance:

    matrix_y = []

    for out_y in output_distance:

        #z1 = math.sqrt((float(out_x[6])) ** 2 + (float(out_x[7])) ** 2 + (float(out_x[8])) ** 2)
        #z2 = math.sqrt((float(out_y[6])) ** 2 + (float(out_y[7])) ** 2 + (float(out_y[8])) ** 2)
        zwischenergebnis_1 = math.sqrt((float(out_x[6]) - float(out_y[6])) ** 2 +
                                       (float(out_x[7]) - float(out_y[7])) ** 2 +
                                       (float(out_x[8]) - float(out_y[8])) ** 2)

        if abs(zwischenergebnis_1) < args.distance and not (out_x[4] == out_y[4] and out_x[5] == out_y[5]):
            matrix_y.append(1)
        else:
            matrix_y.append(0)

    matrix_x.append(matrix_y)


"""" Matrix ausgeben """

# speichert den finale Matrix Inhalt
matrix_inhalt = "\t"

# hinzufuegen der ersten Zeile zum Matrixinhalt:
# die erste Zeile gibt die x Achse aus
# jede AS wird ueber 3-Buchstabenabkuerzung_Strang_AS-Nummer identifiziert
for od in output_distance:
    matrix_inhalt += od[3]
    matrix_inhalt += "_"
    matrix_inhalt += od[4]
    matrix_inhalt += "_"
    matrix_inhalt += od[5]
    matrix_inhalt += "\t"

matrix_inhalt += "\n"


var = 0
while var < len(matrix_x):

    # hinzufuegen der ersten Zeile
    line = ""

    line += output_distance[var][3]
    line += "_"
    line += output_distance[var][4]
    line += "_"
    line += output_distance[var][5]
    line += "\t"

    for matr_x in matrix_x[var]:

        line += str(matr_x)
        line += "\t"

    #print(line)
    line += "\n"
    matrix_inhalt += line
    var += 1

# Speichern der Matrix in eine Datei
if not args.output == None:
    filename2 = args.output + "/" + args.id + "_matrix.tsv"
    with open(filename2, "w", encoding="UTF-8", newline="") as out_file:
        out_file.write(matrix_inhalt)
#else:
    #print(matrix_inhalt)


plt.imshow(matrix_x, interpolation='none')


if not args.output == None:
    path = (args.output + "/" + args.id + "_2D_abb.png")

    plt.savefig(path)
else:
    plt.show()

