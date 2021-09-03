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
    train = subprocess.call(["java", "-jar", "/mnt/biocluster/praktikum/bioprakt/progprakt-e/Solution3/finaljars/gor_validation.jar", "-p",org_path ,"-r", feat_path, "-f", "txt", "-s", "./uploads/summary_validation.txt", "-d", "./uploads/detail_validation.txt"])
    output = True

    #if output != None: #process output format
    #    output = output.decode('utf-8', 'ignore')
    #    output = output.replace(" ", "\n")
    #else:
    #    output = "File could not be processed."


env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('gor_validation_template.html')


print(template.render(
    title='GOR Validation',
    header='GOR Validation',
    output=output
))
