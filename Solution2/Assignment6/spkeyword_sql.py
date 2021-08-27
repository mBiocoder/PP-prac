#!/usr/bin/env python3

import argparse as ap

import pymysql

parser = ap.ArgumentParser()
parser.add_argument("--keyword", required=True, nargs='+')

#specify arguments
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument("--swissprot",  type=ap.FileType('r'))
group.add_argument("--db", action = 'store_true')


#parse arguments
args = parser.parse_args()
keywords = args.keyword
swissprot = args.swissprot
db = args.db

#convert keywords to lowercase for improved matching process (optional)
#keywords = [x.lower() for x in keywords]

#function that returns boolean value reflecting whether a given list contains at least one of the given key words

def compareContent(list_kw):
    status = False
    #return true if at least one keyword can be found in the list to be searched
    if not set(keywords).isdisjoint(list_kw):
        status = True
    return status

#read swissprot file and keep the "AC" IDs whose keywords offer up a match with the given search terms
if swissprot != None:
    list_ac = []
    ac = []
    kw = []
    for line in swissprot:
        if (line.startswith('//')):
            # call function to check for keyword matches; add to list if there are such matches
            if (compareContent(kw) == True):
                # list_ac.append(",".join([str(k) for k in key]))
                for a in ac:
                    list_ac.append(a)
            ac = []
            kw = []
        if (line.startswith('AC')):
            # save content in "AC" lines to a sorted list
            if not ac:
                ac = line.replace("AC   ", "").replace(";", "").replace(".", "").split()
                ac = [a.strip() for a in ac]
            else:
                ac_temp = line.replace("AC   ", "").replace(".", "").split(";")
                ac = ac + ac_temp
                ac = [a.strip() for a in ac]
            ac = list(filter(None, ac))
        if (line.startswith('KW')):
            # save content in "KW" lines to a list
            if not kw:
                kw = line.replace("KW   ", "").replace(".", "").split(";")
                kw = [k.strip() for k in kw]
            else:
                kw_temp = line.replace("KW   ", "").replace(".", "").split(";")
                kw = kw + kw_temp
                kw = list(filter(None, kw))
                kw = [k.strip() for k in kw]
            # convert to lowercase for matching process (optional)
            # value = [x.lower() for x in value]

        if (compareContent(kw) == True):
            # list_ac.append(",".join([str(k) for k in key]))
            for a in ac:
                list_ac.append(a)

        # sort and print out final list of ACs
        for l in sorted(set(list_ac)):
            print(l)


elif db == True:
    #list =[0][2]
    ac_list = []
    connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user="biopraktE", passwd="JuM3IZy.8SC0c",
                                 database="biopraktE")

    cursor = connection.cursor()

    for key in keywords:
        try:
            query = f"""SELECT AC FROM Rel_Seq_KW""";
            cursor.execute(query);
            content = cursor.fetchall()
            ac_list.append(content)

        except Exception as e:
            print("Exception occured: " + e);

    for l in sorted(set(ac_list)):
        print(l)



connection.close();
