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
if len(form.getvalue('sequence1', "")) > 0 and len(form.getvalue('sequence2', "")) > 0 and form.getvalue('file3'):
    #error_notice = 'sequence1'
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

#form to get sequences
elif len(form.getvalue('pdb1', "")) > 0 and len(form.getvalue('pdb2', "")) >0 and form.getvalue('file3'):
    #error_notice = 'pdb1'
    id1_fromUser = form.getvalue('pdb1')
    id2_fromUser = form.getvalue('pdb2')

    output_ID1 = subprocess.check_output(["/usr/bin/python3", "./get_PDB.py", "--id",id1_fromUser])
    if output_ID1 != None:  # process output format
       seq1_fromUser = output_ID1.decode('utf-8', 'ignore').strip()

    output_ID2 = subprocess.check_output(["/usr/bin/python3", "./get_PDB.py", "--id", id2_fromUser])
    if output_ID2 != None:  # process output format
       seq2_fromUser = output_ID2.decode('utf-8', 'ignore').strip()
    #print(output_ID1)
    #print(output_ID2)
    input_pairs = os.path.join(UPLOAD_DIREC, "pdb.pairs")
    input_seqlib = os.path.join(UPLOAD_DIREC, "pdb.seqlib")
    input_matrix = os.path.join(UPLOAD_DIREC, "pdb.mat")
    #if sequences are the direct input, not files
    file_pairs = open(input_pairs, "w")
    file_seqlib = open(input_seqlib, "w")

    file_pairs.write("ID1 ID2")
    file_seqlib.write("ID1:"+ seq1_fromUser + "\n")
    file_seqlib.write("ID2:"+ seq2_fromUser + "\n")

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
    #error_notice = 'file'
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
    error_notice = " "

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
    error_notice = " "

#html output
formText = """
        <form action="./alignment_main.py" method="POST" enctype="multipart/form-data">
        <p><b>Please upload your files here:</b></p>
        Pairs file: <input name = "file" type = "file"> </br></br> Seqlib file: <input name = "file2" type = "file"></br></br>
        Matrix file: <input name = "file3" type = "file"></br></br>
        <p><b>Please input your sequences here:</b></p>
	Sequence 1:<br><br>
	<textarea id="sequence1" name="sequence1" rows="5" cols="70"></textarea><br><br>
	Sequence 2:<br><br> 
	<textarea id="sequence2" name="sequence2" rows="5" cols="70"></textarea><br><br>
	<p><b>Please input your PDB ids here: </b></p>
	PDB ID 1:<input name = "pdb1" type = "text"> PDB ID 2:<input name = "pdb2" type = "text"></br></br>
        <p><b>Choose your alignment type:</b></p>
        </label>Global alignment<input type = "radio" id = "global" name = "mode" value = "global" checked = "checked">
        </label>Local alignment<input type = "radio" id = "local" name = "mode" value = "local"></label>
        </label>Freeshift alignment<input type = "radio" id = "freeshift" name = "mode" value = "freeshift"></label></br></br>
        <p><b>Choose your output format:</b></p>
        </label>Scores<input type = "radio" id = "scores" name = "format" value = "scores" >
        </label>Alignment<input type = "radio" id = "alignment" name = "format" value = "ali"></label>
        </label>HTML<input type = "radio" id = "html" name = "format" value = "html" checked = "checked"></label>
        <br><br>
	<p><b>Set your gap penalty here:</b></p>
        Gap open penalty: <textarea id= "go" name = "go" rows = "1" cols ="10">-15</textarea> Gap extension penalty: <textarea id= "ge" name = "ge" rows = "1" cols ="10">-13</textarea>
        <br><br>
         <b></label> Needleman Wunsch<input type = "checkbox" id = "nw" name = "nw" value = "nw">
         <br><br>
        <input name = "submit" type="submit" value = "Submit">
        </form>
"""

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('alignment_template.html')
print(template.render(
    title = "Alignments",
    header = "Alignments",
    error=error_notice,
    ali_output = ali,
    form = formText
))
