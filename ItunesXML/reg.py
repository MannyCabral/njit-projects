import re

p = re.compile('(\\t)*<key>.*</key><\w*>.*</\w*>')
g = open("NewLibrary.xml","w")

f = open("Library.xml", "r")
for line in f:
    if (p.match(line)):
        key = line[line.find('<key>')+5 : line.find('</key>')].lower().replace(' ', '')
        
        ind = line.rfind('</')
        val = line[line.rfind('>',0,ind)+1 : ind]

        g.write("\t\t\t<"+key+">"+val+"</"+key+">\n")
    else:
        g.write(line)
f.close()

g.close()
