#!/usr/bin/env python3

import argparse as ap
import pymysql
import sys, os
from collections import defaultdict

pfad = '/mnt/biocluster/praktikum/bioprakt/Data/HOMSTRAD_DB/2019_Feb'

name2al_seq = defaultdict(str)
name2fam = defaultdict(str)
fam2name = defaultdict(list)
name2seq = defaultdict(str)

alis = 0

for (root,dirs,files) in os.walk(pfad):
    #print("Root:" + str(root))
    #print("Verzeichnisse: " + str(dirs))
    #print("Dateien: " + str(files))

    #print(str(root).split("/")[-1])
    for file in files:

        if str(file).endswith(".ali"):

            #print(str(root))
            root_string = str(root).replace("\\", "/")
            root_string += "/"
            root_string += str(file)
            #print(root_string)
            #print(str(file))
            alis += 1

            family = ""

            with open(root_string, "r", encoding="UTF-8", newline="") as in_file:

                name = ""

                seqs_in_file = defaultdict(str)

                i = 0
                for line in in_file:


                    if line.startswith("C; family: "):
                        family = (line[11:].strip())

                    elif line.startswith(">"):
                        name = (line.split(";")[1].strip())

                        name2fam[name] = family
                        fam2name[family].append(name)
                        #print(name)

                    elif not line.startswith("C; ") and not "structure" in line:
                        #line = line.replace("*", "")
                        name2al_seq[name] += line.strip()
                        name2seq[name] += line.replace("-", "").strip()
                        seqs_in_file[name] += line.strip()

                i += 1

                for key, val in seqs_in_file.items():

                    #print("ID:\t\t\t" + key)
                    #print("Familie:\t" + family)
                    #print("Sequenz:\t" + seqs_in_file[key])
                    #print("")

                    connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user="biopraktE", passwd="JuM3IZy.8SC0c",
                                                 database="biopraktE")


                    try:
                        with connection.cursor() as cursor:
                            sql = "INSERT INTO alignment(alignment_text, alignment, family) VALUES (%s, %s,%s)"
                            cursor.execute(sql, (key , seqs_in_file[key], family))
                        connection.commit()
                        print("Query was executed successfully!");

                    except Exception as e:
                        print("Exception occured" + str(e));

                    connection.close();
"""
print("Alignment-roh-sequenzen: " + str(name2al_seq))
print("Sequenzen (ohne -): " + str(name2seq))
print("Name -> Familie: " + str(name2fam))
print("Familie -> Name: " + str(fam2name))
print(len(fam2name))
print("es gibt " + str(alis) + " .ali Dateien.")

seqs = 0
for fam in fam2name.values():
    seqs += len(fam)
print("darin sind " + str(seqs) + " viele Sequenzen")

print(len(name2seq))


for key, val in name2seq.items():
    if len(val.split("*")) > 2:
        print(key + ": " +val)
            #print("###################################################################################################")
            """
