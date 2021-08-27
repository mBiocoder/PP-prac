#!/usr/bin/env python3
import argparse as ap
import pymysql

parser = ap.ArgumentParser()

parser.add_argument("--id", type = str, required = True, nargs='+')
parser.add_argument("--source", type = str, required = True)
parser.add_argument("--output", type = ap.FileType('w'), required = False)

args = parser.parse_args()
id = args.id
source = args.source

connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user ="biopraktE" , passwd="JuM3IZy.8SC0c" , database="biopraktE")

cursor = connection.cursor()

dict_seq = {}
for i in id:
    id_temp = i
    cursor.execute(f"""SELECT sequence FROM biopraktE.sequence WHERE seq_id ='{i}' AND source = {source}""");
    #cursor.execute("""SELECT * FROM biopraktE.sequence""");
    content = cursor.fetchall()
    dict_seq[i] = content

if args.output == None:
    output = open("get_seq.txt", "w")
else:
    output = args.output

for id, seq in dict_seq.items():
    output.write(id)
    output.write("\n")
    #breakpoint()
    output.write(seq[0][0])
    output.write("\n")
   
output.close()

connection.close()
