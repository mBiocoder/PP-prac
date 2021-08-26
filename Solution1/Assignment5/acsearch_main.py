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
output = None

seqid = form.getvalue('seqid') 
if not 'seqid' in form or seqid is None:
    output = None
else :
    output = subprocess.check_output(["/usr/bin/python3", "./acsearch.py", "--ac", seqid]) #run external script and save output to variable
    if output != None: #process output format
        output = output.decode('utf-8', 'ignore')
        #output = output.replace(" ", "\n")
#else:
#    output = "File could not be processed."


formText = """
        <form action="./acsearch_main.py" method="POST" enctype="multipart/form-data">
        Seq-ID: <input name="seqid">
        <input name = "submit" type="submit"> 
	</form>
"""

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('acsearch_template.html')

print(template.render(
    title='Swissprot',
    header='Swissprot Search',
    swiss_output=output,
    form = formText
))
