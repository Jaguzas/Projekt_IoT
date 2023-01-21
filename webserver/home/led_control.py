#!/usr/bin/env python
from sense_emu import SenseHat
import getopt, sys

sense = SenseHat()
def main(args):
	try:
		opts, args = getopt.getopt(sys.argv[1:], "x:y:r:g:b:")
	except getopt.GetoptError as err:
		print(err)
		sys.exit(2) 
	x = 0
	y = 0
	r = 0
	g = 0
	b = 0
	
	for o, a in opts:
		if o == "-r":
			r = int(a)                 
		elif o == "-g":           
			g = int(a)           
		elif o == "-b":           
			b = int(a)            
		elif o == "-x":           
			x = int(a)            
		elif o == "-y":           
			y = int(a)    
		else:
			assert False, "nieznana opcja"


	print("x={},y={},r={},g={},b={}".format(x,y,r,g,b))
	sense.set_pixel(x,y,r,g,b)

if __name__ == '__main__':
	import sys
	sys.exit(main(sys.argv))
