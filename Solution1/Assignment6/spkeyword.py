#!/usr/bin/env python3

import argparse as ap

parser = ap.ArgumentParser()

#specify arguments
parser.add_argument("--keyword", required=True, nargs='+')
parser.add_argument("--swissprot", required=True)

#parse arguments
args = parser.parse_args()
keywords = args.keyword
swissprot = args.swissprot

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
with open(swissprot) as file:
    list_ac = []
    ac = []
    kw = []
    for line in file:
        if (line.startswith('//')):
            #print(ac)
            #print(kw)
            #call function to check for keyword matches; add to list if there are such matches
            if (compareContent(kw) == True):
                #list_ac.append(",".join([str(k) for k in key]))
                for a in ac:
                    list_ac.append(a)
            ac = []
            kw = []
        if (line.startswith('AC')):
            #save content in "AC" lines to a sorted list
            if not ac:
                ac = line.replace("AC   ", "").replace(";","").replace(".","").split()
                ac = [a.strip() for a in ac]
            else:
                ac_temp = line.replace("AC   ", "").replace(".", "").split(";")
                ac = ac + ac_temp
                ac = [a.strip() for a in ac]
            ac = list(filter(None, ac))
        if (line.startswith('KW')):
            #save content in "KW" lines to a list
            if not kw:
                kw = line.replace("KW   ","").replace(".","").split(";")
                kw = [k.strip() for k in kw]
            else:
                kw_temp = line.replace("KW   ","").replace(".","").split(";")
                kw = kw + kw_temp
                kw = list(filter(None,kw))
                kw = [k.strip() for k in kw]
            #convert to lowercase for matching process (optional)
            #value = [x.lower() for x in value]
if (compareContent(kw) == True):
    #list_ac.append(",".join([str(k) for k in key]))
    for a in ac:
        list_ac.append(a)
#print(ac)
#print(kw)

#sort and print out final list of ACs
for l in sorted(set(list_ac)):
    print(l)
