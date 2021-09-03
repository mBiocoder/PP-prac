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
output = False

if 'train_file' in form:
    train_file = form['train_file']
    gor_version = form.getvalue('option_GOR')
    if train_file.filename:
        train_path = os.path.join(dir, os.path.basename(train_file.filename))
        open(train_path, 'wb').write(train_file.file.read()) #save file to specified directory
    p_train = os.getcwd() + "/" + train_path #dynamically access current directory and create full path var to location of text file
    p_model= ""
    #Generate output
    train = subprocess.call(["java",  "-jar", "./finaljars/train.jar", "--db", p_train, "--method", gor_version, "--model", "./uploads/model_trained.tsv"])
    output = True

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('gor_train_template.html')

print(template.render(
    title='GOR Training',
    header='GOR Training',
    output=output
))

