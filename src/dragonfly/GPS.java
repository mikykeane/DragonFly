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
    public boolean beenHere[][] = new boolean [1000][1000];
    /**
     * @author María del Mar García Cabello y Miguel Keane
     * @param object
     */
    public void GPSParser(JsonObject object)
    {
        //Parseamos los datos de JSON 
        //Obtenemos la dirección en la que nos despertamos
        y= object.get("perceptions").asObject().get("gps").asObject().get("x").asInt();
        x= object.get("perceptions").asObject().get("gps").asObject().get("y").asInt();
        z= object.get("perceptions").asObject().get("gps").asObject().get("z").asInt();
        
        beenHere[x][y]=true; 
        
        
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
