#!/usr/bin/python3

import pymysql

connection = pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de", user ="biopraktE" , passwd="JuM3IZy.8SC0c" , database="biopraktE")

cursor= connection.cursor()
delete_existing_DB= "drop table if exists DB";
delete_existing_keywords= "drop table if exists keywords";
delete_existing_alignment= "drop table if exists alignment";
delete_existing_sequence= "drop table if exists sequence";
delete_existing_Rel_Seq_KW= "drop table if exists Rel_Seq_KW";

#create all needed tables
create_table_DB = """create table DB (db_ID int auto_increment primary key, source varchar(100))""";

create_table_keywords = """create table keywords (keyword_ID int primary key, keyword varchar (100), category varchar(100))""";

create_table_alignment = """create table alignment (alignment_ID int auto_increment primary key, alignment_text varchar(100), alignment text(1000), family varchar(100))""";

create_table_sequence = """ create table sequence (seq_ID_inc int auto_increment primary key,seq_id varchar(100),  sequence text,  type varchar(100), organism varchar(100), db_ID int, source varchar(100), alignment_ID int, parent text)""";


create_table_Rel_Seq_KW = """create table  Rel_Seq_KW (seq_ID_inc int, keyword_ID int, primary key(seq_ID_inc, keyword_ID))""";

#print if successfully created
try:
    cursor.execute(delete_existing_DB);
    print("Existing table DB has been deleted.");

    cursor.execute(delete_existing_keywords);
    print("Existing table sequence has been deleted.");

    cursor.execute(delete_existing_alignment);
    print("Existing table sequence has been deleted.");

    cursor.execute(delete_existing_sequence);
    print("Existing table sequence has been deleted.");

    cursor.execute(delete_existing_Rel_Seq_KW);
    print("Existing table sequence has been deleted.");



    cursor.execute(create_table_DB);
    print("DB table has been created!");

    cursor.execute(create_table_keywords);
    print("keywords table has been created!");

    cursor.execute(create_table_alignment);
    print("Alignment table has been created!");

    cursor.execute(create_table_sequence);
    print("Sequence table has been created!");

    cursor.execute(create_table_Rel_Seq_KW);
    print("Rel-Seq-KW table has been created!");

except Exception as e:
    print("Exception occured");

connection.close();
