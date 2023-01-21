#!/usr/bin/env python
import json
import getopt, sys
from sense_emu import SenseHat


def main(args):
    try:
        opts, args = getopt.getopt(sys.argv[1:], "rpyu:")
    except getopt.GetoptError as err:
        print(err)
        sys.exit(2)
    sense = SenseHat()
    # jezeli False, to katy
    radian = True
    orient_rad = sense.get_orientation_radians()   
    orient_deg = sense.get_orientation_degrees()    
    
    for o, a in opts:
        if o == "-u":
            if a == "s":
                radian = False
            elif a == "r":
                radian = True
            else:
                print("Zla jednostka")
                sys.exit()
    
    for o, a in opts:
        if o == "-r":
            if radian:
                r = {"name": "roll", "value" : orient_rad["roll"], "unit" : "rad"}                       
            else: 
                r = {"name": "roll", "value" : orient_deg["roll"], "unit" : "deg"}        
            print(json.dumps(r))            
        elif o == "-p":           
            if radian:
                p = {"name": "pitch", "value" : orient_rad["pitch"], "unit" : "rad"}                       
            else: 
                p = {"name": "pitch" , "value" : orient_deg["pitch"], "unit" : "deg"}        
            print(json.dumps(p)) 
        elif o == "-y":
            if radian:
                y = {"name": "yaw", "value": orient_rad["yaw"], "unit" : "rad"}                       
            else: 
                y = {"name": "yaw", "value": orient_deg["yaw"], "unit" : "deg"}        
            print(json.dumps(y)) 
        elif o <> "-u":
            assert False, "nieznana opcja"

if __name__ == '__main__':
    import sys
    sys.exit(main(sys.argv))
