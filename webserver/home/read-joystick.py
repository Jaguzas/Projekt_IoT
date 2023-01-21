#!/usr/bin/env python
from sense_emu import SenseHat
import json

plikwyj="/home/pi/joystick.dat";

hat = SenseHat()
x = 0
y = 0
srodek = 0

def obsluga(event):
	global x, y, srodek	
	zmiana = False
	if event.direction=="middle" and event.action=="pressed":
		srodek += 1	
		zmiana = True	
	if event.direction=="right" and event.action=="pressed":
		x += 1
		zmiana = True	
	if event.direction=="left" and event.action=="pressed":
		x -= 1
		zmiana = True	
	if event.direction=="up" and event.action=="pressed":
		y += 1
		zmiana = True	
	if event.direction=="down" and event.action=="pressed":
		y -= 1
		zmiana = True	
	if zmiana:
		dane = {"x":x, "y":y, "m":srodek }
		#print(json.dumps(dane))
		with open(plikwyj, "w") as f:
			f.write(json.dumps(dane))



hat.stick.direction_up = obsluga
hat.stick.direction_down = obsluga
hat.stick.direction_left = obsluga
hat.stick.direction_right = obsluga
hat.stick.direction_middle = obsluga

# zerujemy plik danych z joystika na poczatku:
with open(plikwyj, "w") as f:
	f.write('{"x":0,"y":0,"m":0}\n');	
	
while True:
	k = raw_input("Press Enter to exit...")
	if k == "":
		break

	
