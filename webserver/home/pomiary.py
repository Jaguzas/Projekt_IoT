#!/usr/bin/env python
import json
import getopt, sys
from sense_emu import SenseHat


def main(args):
    try:
        opts, args = getopt.getopt(sys.argv[1:], "h:p:t:")
    except getopt.GetoptError as err:
        print(err)
        sys.exit(2)
    sense = SenseHat()
    for o, a in opts:
        if o == "-h":
            humidity = sense.get_humidity()
            if a=="p":
                h = { "name" : "wilgotnosc",
                      "value" : humidity, 
                      "unit" : "%"
                    }        
                print(json.dumps(h))
            elif a=="l":
                h = {"name": "wilgotnosc",
                     "value": humidity/100, 
                     "unit" : "[0-1]"}        
                print(json.dumps(h))
            else:
                print("podaj 'p' (procent) lub 'l' (liczba)\n")
        elif o == "-p":
            if a=="h":
                pressure = sense.get_pressure()
                p = { "name" : "cisnienie",
                      "value" : pressure, 
                      "unit" : "hPa"}        
                print(json.dumps(p))
            elif a=="m":
                pressure = sense.get_pressure()
                p = { "name": "cisnienie",
                "value":pressure*0.75, 
                "unit" : "mmHg"}        
                print(json.dumps(p))
            else:
                print("podaj 'h' (hPa) lub 'm' (mmHg)\n")
            
        elif o == "-t":
            if a=="c":
                temperature = sense.get_temperature()
                t = {"name": "temperatura",
                "value": temperature, "unit" : "C"}        
                print(json.dumps(t))
            elif a=="f":
                temperature = sense.get_temperature()
                t = {"name":"temperatura",
                "value": (temperature*1.8)+32, "unit" : "F"}        
                print(json.dumps(t))
            else:
                print("podaj 'c' (Celsjusz) lub 'f' (Fahrenheit)\n")	    
        else:
            assert False, "nieznana opcja"

if __name__ == '__main__':
    import sys
    sys.exit(main(sys.argv))
