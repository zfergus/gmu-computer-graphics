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

	y = ySign * (h/2.0) * random.random()
	
	xOnCir = r * math.cos(math.asin(y/r if math.fabs(y) <= r else 1))
	x = xSign * ((w/2.0 - xOnCir) * random.random() + xOnCir)
	
	f.write("%f %f%s" % (x, y, "\n" if i+1<numPoints else ""))
	
f.close()