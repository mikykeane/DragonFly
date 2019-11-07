/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.JsonObject;

/**
 * Clase dónde controlaremos la posición del agente en el mundo. 
 * @author Miguel Keane
 */
public class GPS {
    int x;
    int y;
    int z;
    
    /**
     * @author María del Mar García Cabello
     * @param object
     */
    public void GPSParser(JsonObject object)
    {
        //Parseamos los datos de JSON 
        //Obtenemos la dirección en la que nos despertamos
        x= object.get("x").asObject().asInt();
        y= object.get("y").asObject().asInt();
        z= object.get("z").asObject().asInt();
        
    }
    
     /**
     * @author María del Mar García Cabello
     * @return x
     */
    public int getX() {
        return x;
    }

     /**
     * @author María del Mar García Cabello
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * @author María del Mar García Cabello
     * @return z
     */
    public int getZ() {
        return z;
    }
    
   
}
