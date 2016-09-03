import random
import math

w = 800
h = 800
r = 200

numPoints = 100000;
f = open("points.txt", "w")

for i in range(0, numPoints):
	xSign = 1 if random.random() >= 0.5 else -1
	ySign = 1 if random.random() >= 0.5 else -1

	if(random.random() >= 0.5):
		x = xSign * ((w/2.0) * random.random())
		y = ySign * ((h/2.0-r) * random.random() + r)
	else:
		x = xSign * ((w/2.0-r) * random.random() + r)
		y = ySign * ((h/2.0) * random.random())
		
	f.write("%f %f%s" % (x, y, "\n" if i+1<numPoints else ""))
	
f.close()