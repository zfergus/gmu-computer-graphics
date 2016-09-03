import random
import math

def normalizeVector(x, y):
	magnitude = math.sqrt(x*x+y*y)
	return (x/magnitude, y/magnitude)

def main():
	print("Generating random points:")
	
	w = 800
	h = 800
	r = 200

	numPoints = 100000;
	f = open("points.txt", "w")

	for i in range(0, numPoints):
		xSign = 1 if random.random() >= 0.5 else -1
		ySign = 1 if random.random() >= 0.5 else -1

		x = xSign * random.random()
		y = ySign * random.random()

		(x, y) = normalizeVector(x, y)
		
		x = x * (random.random() * (w/2.0-r) + r)
		y = y * (random.random() * (h/2.0-r) + r)
			
		f.write("%f %f%s" % (x, y, "\n" if i+1<numPoints else ""))
		
		counter = "%d%%" % (math.ceil(i/(numPoints*1.0) * 100))
		print("%s%s"%(counter, "\b" * len(counter)), end = "")
		
	f.close()
	print("")

if __name__ == "__main__":
	main();