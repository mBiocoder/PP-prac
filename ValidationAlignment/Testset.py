#!/usr/bin/env python3

"""
Idee: Wir holen unsere Sequenzen aus unserer Datenbank, die wir gemäß Aufgabenblatt 2 Aufgabe 14 in unsere Datenbank eingefügt haben
über SQL queries heraus. Um ein random test set zu erzeugen, wollen wir die ids random samplen und diese samples in ein
file schreiben. Das ist unser testset.
"""

import pymysql
import random
from random import randint

connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user ="biopraktE" , passwd="JuM3IZy.8SC0c" , database="biopraktE")
cursor = connection.cursor()

#get a random header and random score
header = random.sample(('>5_3_exonuclease_0_1', '>6PF2K_0_1', '>7kD_DNA_binding_0_1', '>6PGD_0_1 774.4', '>A2M_A_0_1'), 1)
score = random.sample(('30.0', '210.5', '33.3', '12.7', '95.8'), 1)

try:
    with connection.cursor() as cursor:
        sql = "SELECT alignment FROM alignment WHERE alignment_ID = (%s)"
        for times in range(1,5):
            #create random access_id for database
            times = random.randint(1, 20)
            cursor.execute(sql, (times))
            content = cursor.fetchall()
            # Open a file with access mode 'a'
            with open("testset.txt", "a") as file_object:
                file_object.write(str(header).strip('[]') + " " + str(score).strip('[]') + "\n" +str(times)+ ": " +str(content[0][0]) + "\n")
    connection.commit()
    print("Query was executed successfully!");

except Exception as e:
    print("Exception occured" + str(e));

connection.close();
