#!/usr/bin/env python3

import argparse as ap
from collections import defaultdict
import sys, os
import pymysql
import regex as re

parser = ap.ArgumentParser()

#specify arguments
parser.add_argument("--input", required=True)

#parse arguments
args = parser.parse_args()
swissprot = args.input

#read swissprot file and keep the "AC" IDs whose keywords offer up a match with the given search terms

list_dict = []
block_dict = defaultdict(str)
db_dict = defaultdict(str)
db_list = []

counter = 1
recordName = '1'

#Klasse mit key values die 1 entry erstellt -> ID, seq,..., > Liste aus Klassen

with open(swissprot) as file:

    for line in file:

        if (line.startswith('//')):
            counter += 1
            recordName = str(counter)

        else:

            if recordName in block_dict.keys():
                block_dict[recordName] += line
            else:
                block_dict[recordName] = line

#print(block_dict)

for k,v in block_dict.items():

    for line in v.splitlines():

        if (line.startswith('ID')):
            db_dict['ID'] = re.split(r'    ',line)[0].replace("ID   ", "")

        if(line.startswith('SQ')):
            #next(v.splitlines()) #erste Zeile überspringen, next(line)
            #db_dict['SQ'] = re.split(r'    ',line)[0].replace("SQ   ", "")
            db_dict['SQ'] += " ".join(line.split(r'\n')).strip() #sequence

        if(line.startswith('OS')):
            db_dict['OS'] = re.split(r'    ',line)[0].replace("OS   ", "").replace(".","")

        db_list.append(db_dict)

print(db_list)

#print(db_list)

"""
DB
"""

connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user="biopraktE", passwd="JuM3IZy.8SC0c",
                             database="biopraktE")

cursor = connection.cursor()

db2_dict = defaultdict(str)

len(db_list)

seq_ID_inc = 2

for eintrag in db_list:
    for dic in range(len(db_list)):
        seq_id = ''
        sequence = ''
        type = 'AS'
        organism = ''

        for key, value in db_dict.items():
            if (key == 'ID'):
                seqid = value
            if (key == 'SQ'):
                seq = value
            if (key == 'OS'):
                organism = value
        query = """INSERT INTO sequence(seq_id, sequence, type, organism) VALUES ({si}, {s}, {t}, {o})""".format(si = seqid, s = seq, t = type, o = organsim)

        try:
            cursor.execute(query);
            print("Query was executed successfully!");

        except Exception as e:
            print("Exception occured" + str(e));

        connection.close();
