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
output = None

if 'file' in form:
    f_upload = form['file']
    f_kw = form.getvalue('keywords')
    if f_upload.filename:
        f_path = os.path.join(dir, os.path.basename(f_upload.filename))
        open(f_path, 'wb').write(f_upload.file.read()) #save file to specified directory
    p = os.getcwd() + "/" + f_path #dynamically access current directory and create full path var to location of text file
    output = subprocess.check_output(["/usr/bin/python3", "./spkeyword.py", "--keyword", f_kw, "--swissprot", p]) #run external script and save output to variable   
    if output != None: #process output format
        output = output.decode('utf-8', 'ignore')
        output = output.replace(" ", "\n")
    #else:
    #    output = "File could not be processed."


formText = """
        <form action="./spkeyword_main.py" method="POST" enctype="multipart/form-data">
        Keywords: <input name="keywords">
        File: <input name="file" type="file">
        <input name = "submit" type="submit"> 
	</form>
"""

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('spkeyword_template.html')

print(template.render(
    title='Spkeyword',
    header='Swissprot Keyword',
    SP_output=output,
    form = formText
))

