#!/usr/bin/python3

# Import Basic OS functions
# Import modules for CGI handling
import cgi, cgitb, jinja2 
# import urllib.request
import os
import subprocess
import sys
import pylab
# enable debugging
cgitb.enable()
# print content type
print("Content-type:text/html\r\n\r\n")
form = cgi.FieldStorage()

#attributes
error_notice = None
ali = None
seq1_fromUser = None
seq2_fromUser = None

user_input = ""
list_of_all_parameters = []


input_file = None

#access uploads directory
UPLOAD_DIREC = 'uploads'

#form to get sequences
if form.getvalue('sequence1'):
    seq1_fromUser = form.getvalue('sequence1')

    input_file = os.path.join(UPLOAD_DIREC, "user.txt")

    #if sequences are the direct input, not files
    file = open(input_file, "w")
    file.write(seq1_fromUser + "\n")
    file.close()

elif "file1" in form:
    form_file = form['file1']

    input_file = os.path.join(UPLOAD_DIREC, "upload.txt")

    with open(input_file, 'wb') as fout:
            while True:
                chunk = form_file.file.read(100000)
                if not chunk:
                    break
                fout.write(chunk)
else:
    ali = None
    error_notice = "Select your files please!"

#if sequences are the input from user
if not error_notice:
    generated_files = ['--f', input_file]
    cmds = ['java', '-jar', './validateAli.jar'] + generated_files
    proc = subprocess.Popen(cmds, stdout=subprocess.PIPE,
                        stderr=subprocess.STDOUT)
    response = proc.communicate()
    #decode for utf-8
    ali = response[0].decode('UTF-8')
    error_notice = " ".join(cmds)

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('validation_ali_main.html')

print(template.render(
    error=error_notice,
    ali_output = ali
))
