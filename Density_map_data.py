import requests
import json
import sys
import random

############################################
def redondearCoordenadas(num):
    a = "%.6f" % num
    red = float(a)
    return(red)
###########################################

def coordAnt():
    latitud = redondearCoordenadas(random.uniform(4.40, 4.446))
    longitud = redondearCoordenadas(random.uniform(51.20, 51.226))
    
    localizacion = str(longitud) + "," + str(latitud)
    return(localizacion)    
###########################################

def coordHel():
    latitud = redondearCoordenadas(random.uniform(24.90, 24.957))
    longitud = redondearCoordenadas(random.uniform(60.153, 60.175))
    
    localizacion = str(longitud) + "," + str(latitud)
    return(localizacion)
###########################################
def getCoordinates(cit):

    if (cit == '0'):
        coor1 = coordAnt()
        coor2 = coordAnt()
        coor3 = coordAnt()
        coor4 = coordAnt()
        
        heat = random.randint(1,10)
        
        jso = json.dumps([{"coordinates": (coor1, coor2, coor3, coor4)}, {"HeatPercentage": heat*10}])

        return(jso)
    else:
        coor1 = coordHel()
        coor2 = coordHel()
        coor3 = coordHel()
        coor4 = coordHel()
        
        heat = random.randint(1,10)
        
        jso = json.dumps([{"coordinates": (coor1, coor2, coor3, coor4)}, {"HeatPercentage": heat*10}])
        
        return(jso)
