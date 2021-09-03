#!/usr/bin/python3

# Import Basic OS functions
# Import modules for CGI handling
import cgi, cgitb, jinja2
#import urllib.request
import os
import subprocess

# enable debugging
cgitb.enable()
# print content type
print("Content-type:text/html\r\n\r\n")


form = cgi.FieldStorage()
dir = 'uploads'
predictions = None

if 'ss' in form and 'seq' in form:
    ss_text = form.getvalue('ss')
    seq_text = form.getvalue('seq')
    gor_version = form.getvalue('option_GOR')
    write_ss = subprocess.call(['echo', ss_text], stdout=open('uploads/ss_input.txt', 'wb'))
    write_seq  = subprocess.call(['echo', seq_text], stdout=open('uploads/seq_input.fasta', 'wb'))
    p_model= ""
    if gor_version == "gor1" or gor_version == "gor5":
        p_model = "./Model_Files/Model_Output_Gor1.tsv"
    if gor_version == "gor3":
        p_model = "./Model_Files/Model_Output_Gor3.tsv"
    if gor_version == "gor4":
        p_model = "./Model_Files/Model_Output_Gor4.tsv" 
    #Generate output
    if gor_version == "gor5":
        predictions = subprocess.check_output(["java", "-jar", "./finaljars/predict.jar", "--model", p_model, "--format", "html", "--maf", "/home/p/princeh/mnt/biocluster/praktikum/bioprakt/Data/GOR/CB513/CB513MultipleAlignments/"])
    else:
        predictions = subprocess.check_output(["java",  "-jar", "./finaljars/predict.jar", "--model", p_model, "--format", "html", "--seq", "uploads/seq_input.fasta"])
    #stdout, stderr = output.communicate()
    #predictions = stdout.decode('utf-8').splitlines(True)
    if predictions != None:
        predictions = predictions.decode('utf-8')
    #predictions = predictions.replace(",", "\n")

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('gor_textinput_template.html')

print(template.render(
    title='GOR',
    header='GOR Prediction',
    pred_output=predictions
))
