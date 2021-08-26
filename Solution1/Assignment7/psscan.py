#!/usr/bin/env python3

# Bsp Pattern: [LIVMF]-H-C-x(2)-G-x(3)-{STC}-[STAGP]-x-[LIVMFY]

# arg parser:

# importe:
import argparse as ap
import sys, os
import urllib
import re

from collections import defaultdict

# argparser
parser = ap.ArgumentParser()
parser.add_argument('--fasta', type=ap.FileType('r'), default=sys.stdin, required=True)
parser.add_argument('--pattern', type=str, required=False, nargs='+')
parser.add_argument('--web', type=str, required=False, nargs='+')
parser.add_argument('--output', type=ap.FileType('w'), default=sys.stdout, required=False)
args = parser.parse_args()

"""
FASTA einlesen
"""
head2seq = defaultdict(str)

#for infile in args.fasta:

for line in args.fasta:#infile:

    line = line.strip()

    if line.startswith(">"):

        #recordName = line.split()[0][1:]
        recordName = line[1:]

    else:
        head2seq[recordName] += line


#print(head2seq)


""" Methode, die aus der Website den prosite pattern extrahiert: """
def extract_pattern_from_web(prosite_pattern_id):

    """ entsprechende Website einlesen """
    from urllib.request import urlopen
    response = urlopen('https://prosite.expasy.org/{prospat}'.format(prospat=prosite_pattern_id))

    source_code = response.read().decode('utf-8', 'ignore')
    print(source_code)

    pattern_aus_web = re.findall(r'"inline">.*-.*[.]', source_code)
    print(pattern_aus_web[0][9:-1])
    return pattern_aus_web[0][9:-1]



""" Methode, die einen prosite pattern in einen pattern uebersetzte, der regex kompatibel ist """
def translate_pattern(prosite_pattern):

    """ Ueberarbeitung """
    prosite_pattern = prosite_pattern.replace("{", "[\\")
    prosite_pattern = prosite_pattern.replace("}", "]")
    prosite_pattern = prosite_pattern.replace("<", "^")
    prosite_pattern = prosite_pattern.replace(">", "$")
    prosite_pattern = prosite_pattern.replace("x(", ".{")
    prosite_pattern = prosite_pattern.replace(")", "}")
    prosite_pattern = prosite_pattern.replace("x", ".{1,1}")
    prosite_pattern = prosite_pattern.replace("-", "")

    #print(prosite_pattern)

    """
    erster Versuch diese Aufgabe zu loesen:
    --> wurde bei der Abnahme auf meine Fehler hingewiesen und habe daraufhin das Ganze nochmal ueberarbeitet
        dabei ist mir auch aufgefallen, dass man das viel einfacher mit .replace() loesen kann (siehe oben)
    
    # aufsplitten des prosite patterns nach -
    arr_pat = prosite_pattern.split('-')

    # ergebnis pattern:
    pattern = ""


    for arr_p in arr_pat:

        if arr_p.startswith("<"):
            arr_p = arr_p[1:]
            pattern += "^"

        # ein einfaches x steht fuer ein beliebige Zeichen
        if arr_p == "x":
            pattern += ".{1,1}"

        # eckige Klammern geben an, welche Zeichen hier legitim sind
        # eine einzelne AS gibt an, dass an der Stelle nur diese AS stehen darf
        # beides kann einfach so dem pattern hinzugefuegt werden, da sie beide kompatibel mit regex sind
        if ( arr_p.startswith("[") and arr_p.endswith("]") ) or (len(arr_p) == 1 and not arr_p == "x"):
            pattern += arr_p

        # Teile die mit x beginnen und nicht 'x' sind haben dahinter in Klammer angegeben, wie viele Zeichen beliebig sind
        # (2,4) steht fuer mindestens 2 maximal 4 beliebige Zeichen
        # (2) steht fuer genau 2 Zeichen
        if arr_p.startswith("x") and not arr_p == "x":
            # x wird entfernt
            x_arr_p = arr_p[1:]
            x_arr_p.strip()
            # wenn nur 1 Wert in Klammern steht z.B. x(2) wird das in .{2,2} uebersetzt
            if len(arr_p.split(",")) == 1:
                # Klammern entfernen
                x_arr_p = x_arr_p[1:-1]
                # Aufbau des Patterns
                pattern += ".{"
                pattern += x_arr_p
                pattern += ","
                pattern += x_arr_p
                pattern += "}"
            elif len(arr_p.split(",")) == 2:
                # Klammern entfernen
                x_arr_p = x_arr_p[1:-1]
                x_arr_p = x_arr_p.strip()
                # Aufbau des Patterns
                pattern += ".{"
                pattern += x_arr_p
                pattern += "}"

        # geschweifte Klammern geben an, dass alle Buchstaben ausser den in Klammern angegebenen Zeichen verwendet
        # werden duerfen
        if arr_p.startswith("{") and arr_p.endswith("}"):
            pattern += "[\\"
            pattern += arr_p[1:-1]
            pattern += "]"

        if arr_p.endswith(")") and not arr_p.startswith("x"):
            yyy = ""
            for char in arr_p:
                if char == "(":
                    yyy += "{"
                elif char == ")":
                    yyy += "}"
                else:
                    yyy += char
            pattern += yyy


    # rueckgabe des Patterns
    return pattern
    """
    return prosite_pattern


""" Methode, die in der eigelesenen Fasta Datei nach dem Pattern sucht in jeder Sequenz der Datei 
    es wird ein String zurueckgegeben, der dem Output entspricht"""
def search(pattern):

    # Ergebnis String
    result = ""

    # es werden alle Sequenzen durchlaufen
    for k in head2seq.keys():

        #print(pattern)
        #print(k)
        #print(head2seq[k])

        # Matchobjekt, das den pattern mit der Sequenz matcht
        #match = re.search(pattern, str(head2seq[k]))

        #print(not match == None)

        # es ein Match vorliegt:
        #if not match == None:
            #print("geht nicht!")

        for match in re.finditer(pattern, str(head2seq[k])):

            # Aufbau der Zeile:
            fasta_sequence_name = k
            line = fasta_sequence_name
            line += "\t"

            # Ausgabe der Startposition
            start = match.start()
            line += str(start)
            line += "\t"

            #  Endposition
            end = match.end()

            # Sequenz ist der Bereich zwischen Start und Ende der Fasta Sequenz
            seq = head2seq[k][start:end]
            line += seq
            line += "\n"

            result += line
    #print(result)

    return result

final_output = ""

""" Website einlesen """
if not args.web == None:

    #print("--web ist vorhanden")

    for we in args.web:
        prosite_pattern = extract_pattern_from_web(we)
        pattern = translate_pattern(prosite_pattern)

        final_output += search(pattern)

#else:
    #print("--web fehlt")


""" Pattern einlesen """
if not args.pattern == None:

    #print("--patter ist vorhanden")

    for p in args.pattern:
        pattern = translate_pattern(p)
        #print(pattern)

        final_output += search(pattern)


print(final_output, file=args.output)

#else:
    #print("--pattern fehlt")




"""
string = "-----LHC--G---AS--Hallo--LUUU"
match =re.search(pattern, string)
start = match.start()
"""



