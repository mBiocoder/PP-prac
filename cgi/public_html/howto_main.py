#!/usr/bin/python3

# Import Basic OS functions
# Import modules for CGI handling
import cgi, cgitb, jinja2
#import urllib.request
import os

# enable debugging
cgitb.enable()
# print content type
print("Content-type:text/html\r\n\r\n")

env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = env.get_template('howto_template.html')

print(template.render(
    title='How-To for Git',
    header='How to use git'
))
