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

if 'ss_file' in form and 'seq_file' in form:
    ss_file = form['ss_file']
    seq_file = form['seq_file']
    probs = form.getvalue('option_probs')
    gor_version = form.getvalue('option_GOR')
    if seq_file.filename:
        seq_path = os.path.join(dir, os.path.basename(seq_file.filename))
        open(seq_path, 'wb').write(seq_file.file.read()) 
    if ss_file.filename:
        ss_path = os.path.join(dir, os.path.basename(ss_file.filename))
        open(ss_path, 'wb').write(ss_file.file.read()) 
    p_seq = os.getcwd() + "/" + seq_path 
    p_ss = os.getcwd() + "/" + ss_path 
    p_model= ""
    if gor_version == "gor1" or gor_version == "gor5":
        p_model = "./Model_Files/Model_Output_Gor1.tsv"
    if gor_version == "gor3":
        p_model = "./Model_Files/Model_Output_Gor3.tsv"
    if gor_version == "gor4":
        p_model = "./Model_Files/Model_Output_Gor4.tsv" 
    #Generate output
    #output = subprocess.Popen(["java",  "-jar", "./finaljars/predict.jar", "--model", p_model, "--format", "html", "--seq", p_seq], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    elif gor_version == "gor5":
        predictions = subprocess.check_output(["java", "-jar", "./finaljars/predict.jar", "--model", p_model, "--format", "html", "--seq", p_seq, "--probabilities", probs])
    else:
        predictions = subprocess.check_output(["java",  "-jar", "./finaljars/predict.jar", "--model", p_model, "--format", "html", "--seq", p_seq, "--probabilities", probs])
    if predictions != None:
        predictions = predictions.decode('utf-8')
    #stdout, stderr = output.communicate()
    #predictions = stdout.decode('utf-8').splitlines()
    #predictions = predictions.replace("\n", " ")

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('gor_main_template.html')

print(template.render(
    title='GOR',
    header='GOR Prediction',
    pred_output=predictions
))

