#!/usr/bin/python3

# Import Basic OS functions
# Import modules for CGI handling
import cgi, cgitb, jinja2
# import urllib.request
import os
import subprocess
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

#gap costs
go = ""
ge = ""

input_pairs = None
input_seqlib = None
input_matrix = None

#access uploads directory
UPLOAD_DIREC = 'uploads'

#form to get sequences
if form.getvalue('sequence1') and form.getvalue('sequence2') and form.getvalue('file3'):
    seq1_fromUser = form.getvalue('sequence1')
    seq2_fromUser = form.getvalue('sequence2')

    input_pairs = os.path.join(UPLOAD_DIREC, "user.pairs")
    input_seqlib = os.path.join(UPLOAD_DIREC, "user.seqlib")
    input_matrix = os.path.join(UPLOAD_DIREC, "user.mat")

    #if sequences are the direct input, not files
    file_pairs = open(input_pairs, "w")
    file_seqlib = open(input_seqlib, "w")

    file_pairs.write("Id1 Id2")
    file_seqlib.write("Id1:" + seq1_fromUser + "\n")
    file_seqlib.write("Id2:" + seq2_fromUser + "\n")

    file_seqlib.close()
    file_pairs.close()

    form_file3 = form['file3']
    with open(input_matrix, 'wb') as fout:
        while True:
            chunk3 = form_file3.file.read(100000)
            if not chunk3:
                break
            fout.write(chunk3)


elif "file" in form and "file2" in form and "file3" in form:
    form_file = form['file']
    form_file2 = form['file2']
    form_file3 = form['file3']

    input_pairs = os.path.join(UPLOAD_DIREC, "upload.pairs")
    input_seqlib = os.path.join(UPLOAD_DIREC, "upload.seqlib")
    input_matrix = os.path.join(UPLOAD_DIREC, "upload.mat")

    with open(input_pairs, 'wb') as fout:
            while True:
                chunk = form_file.file.read(100000)
                if not chunk:
                    break
                fout.write(chunk)
    with open(input_seqlib, 'wb') as fout:
        while True:
            chunk2 = form_file2.file.read(100000)
            if not chunk2:
                break
            fout.write(chunk2)
    with open(input_matrix, 'wb') as fout:
        while True:
            chunk3 = form_file3.file.read(100000)
            if not chunk3:
                break
            fout.write(chunk3)

else:
    ali = None
    error_notice = "Selectyour files please!"

#cmdparser similar to alignemnt.jar
if form.getvalue('ge'):
    ge = form.getvalue('ge')
if form.getvalue('go'):
    go = form.getvalue('go')

if go != "":
    goarray = ["--go", go]
    list_of_all_parameters += goarray
if ge != "":
    gearray = ["--ge", ge]
    list_of_all_parameters += gearray
if form.getvalue('mode'):
    modearray = ["--mode", form.getvalue('mode')]
    list_of_all_parameters += modearray
if form.getvalue('format'):
    formatarray = ["--format", form.getvalue('format')]
    list_of_all_parameters += formatarray
if form.getvalue('nw'):
    nwarray =  ["--nw"]
    list_of_all_parameters += nwarray
if form.getvalue('check'):
    checkarray = ["--check", form.getvalue('check')]
    list_of_all_parameters += checkarray

#if sequences are the input from user
if not error_notice:
    generated_files = ['--pairs', input_pairs, '--seqlib', input_seqlib, '-m', input_matrix]
    cmds = ['java', '-jar', './alignment.jar'] + list_of_all_parameters + generated_files
    proc = subprocess.Popen(cmds, stdout=subprocess.PIPE,
                        stderr=subprocess.STDOUT)
    response = proc.communicate()
    #decode for utf-8
    ali = response[0].decode('UTF-8')
    error_notice = " ".join(cmds)

#html output
formText = """
        <font color="blue"><center><h3><b>Sequenz Alignment</b></h3></center></font>
        <form action="./alignment_main.py" method="POST" enctype="multipart/form-data">
        <p><b>File upload</b></p>
        Pairs-Datei: <input name = "file" type = "file"> Seqlib-Datei:<input name = "file2" type = "file"></br></br>
        Matrix-Datei: <input name = "file3" type = "file"></br></br>
        <p><b>Alignieren von einzelnen Sequenzen</b></p>
        Sequenz 1:<input name = "sequence1" type = "text"> Sequenz 2:<input name = "sequence2" type = "text"></br></br>
        <h2><b>Art des Alignments</b></h2>
        </label>Globales Alignment<input type = "radio" id = "global" name = "mode" value = "global" checked = "checked">
        </label>Lokales Alignment<input type = "radio" id = "local" name = "mode" value = "local"></label>
        </label>Freeshift Alignment<input type = "radio" id = "freeshift" name = "mode" value = "freeshift"></label>
        <h2><b>Output format</b></h2>
        </label>Scores<input type = "radio" id = "scores" name = "format" value = "scores" >
        </label>Alignment<input type = "radio" id = "alignment" name = "format" value = "ali"></label>
        </label>HTML<input type = "radio" id = "html" name = "format" value = "html" checked = "checked"></label>
        <br><br>
        <b> Gap strafe: <textarea id= "go" name = "go" rows = "1" cols ="10">-13</textarea> Gap Extensionsstrafe: <textarea id= "ge" name = "ge" rows = "1" cols ="10">-13</textarea>
        <br><br>
         </label> Needelman Wunsch<input type = "checkbox" id = "nw" name = "nw" value = "nw">
         <br><br>
        <input name = "submit" type="submit" value = "Alignment berechnen">
        </form>
"""

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('alignment_template.html')

print(template.render(
    error=error_notice,
    ali_output = ali,
    form = formText
))
