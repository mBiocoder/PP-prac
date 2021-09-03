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

if 'org_file' in form and 'feat_file' in form:
    org_file = form['org_file']
    feat_file = form['feat_file']
    
    if org_file.filename:
        org_path = os.path.join(dir, os.path.basename(org_file.filename))
        open(org_path, 'wb').write(org_file.file.read()) #save file to specified directory
    if feat_file.filename:
        feat_path = os.path.join(dir, os.path.basename(feat_file.filename))
        open(feat_path, 'wb').write(feat_file.file.read()) #save file to specified directory
    p_org = os.getcwd() + "/" + org_path #dynamically access current directory and create full path var to location of text file
    p_feat = os.getcwd() + "/" + feat_path #dynamically access current directory and create full path var to location of text file
    
    #Generate output   
    temp_out1 = subprocess.Popen(["/usr/bin/python3", "./genome2orf.py", "--organism", p_org, "--features", p_feat], stdout=subprocess.PIPE)
    temp_out2 = subprocess.Popen(["/usr/bin/python3", "dna2mrna.py", "--fasta", "-"], stdin=temp_out1.stdout, stdout=subprocess.PIPE)
    temp_out1.wait()
    output = subprocess.check_output(["/usr/bin/python3", "mrna2aa.py", "--fasta", "-"], stdin=temp_out2.stdout)
    temp_out2.wait()

    if output != None: #process output format
        output = output.decode('utf-8', 'ignore')
    #    output = output.replace(" ", "\n")
    #else:
    #    output = "File could not be processed."


formText = """
        <form action="dna2rna_main.py" method="POST" enctype="multipart/form-data">
        Organism file: <input name="org_file" type="file">
        Feature file: <input name="feat_file" type="file"></br></br>
        <input name = "submit" type="submit"> 
	</form>
"""

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('dna2rna_template.html')

print(template.render(
    title='DNA2RNA',
    header='DNA to RNA',
    dna2rna_output=output,
    form = formText
))
