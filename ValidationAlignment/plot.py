#!/usr/bin/env python3
from matplotlib import pyplot as plt
import subprocess

proc = subprocess.Popen(['java', '-jar', '/Users/as/Documents/Alignment/validateAli.jar', "-f", "/Users/as/Documents/Testcases.txt"],stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
response = proc.communicate()
cmd_output= response[0].decode('UTF-8')

print(response)

rows = cmd_output.split("\n")
print(rows)
row_counter = 0

score = []
spec = []
spec1 = []
sens = []
cov = []
meansh = []
inv_meansh = []

for row in rows:

    if row.startswith(">"):
        row_counter += 1
        row = row.split()

        # 1st number after name is score
        score.append(float(row[1]).__round__(2))
        # 2nd number after name is sens (blue)
        sens.append(float(row[2]).__round__(2))
        # 3rd number after name is spec (green)
        spec.append(float(row[3]).__round__(2))
        # 4th number after name is cov
        cov.append(float(row[4]).__round__(2))
        # 5th number after name is mean shift error
        meansh.append(float(row[5]).__round__(2))
        # 6th number after name is the inverse mean shift error
        inv_meansh.append(float(row[6]).__round__(2))

sorted(score)
sorted(spec)
sorted(sens)
sorted(cov)
sorted(meansh)
sorted(inv_meansh)

for value in spec:
    spec1.append(1-value)

sorted(spec1)

def sens_spec():
    #sensitivity in dependeny of specitifity
    plt.plot(spec, sens, '.g')
    plt.title("Correlation between true matches and true mismatches (TP ~ TN)")
    plt.xlabel("specificity")
    plt.ylabel("sensitivity")
    plt.show()
    #(result: with growing specificity the sensitivity grows (proportional dependeny))

sens_spec()

def sens_spec_cov():
    plt.plot(spec, sens, '.g') #green
    plt.plot(spec, cov, '.r') #red
    plt.title("Correlation of specificity and coverage")
    plt.xlabel("specificity")
    plt.ylabel("coverage")
    plt.show()

sens_spec_cov()

def alignment():
    #spec, sens and cov in dependency of the score (alignment)
    plt.plot(score, spec, '.b')
    plt.plot(score, sens, '.g')
    plt.plot(score, cov, '.r')
    plt.title("Sensitivity, specificity and coverage in relation to the score")
    plt.xlabel("score")
    plt.ylabel("sensitivity(green), specificity(blue) and coverage(red)")
    plt.show()

alignment()

def meanSh_cov():
    plt.plot(meansh, cov, '.k')
    plt.title("Correlation between sequence identity and alignment quality")
    plt.xlabel("MSE")
    plt.ylabel("coverage")
    plt.show()

meanSh_cov()


def ROC():
    plt.plot(spec1, sens, '.y')
    plt.title("ROC Curve")
    plt.xlabel("1-specificity")
    plt.ylabel("sensitivity")
    plt.show()

ROC()