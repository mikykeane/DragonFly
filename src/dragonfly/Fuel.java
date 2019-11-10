/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.JsonObject;

/**
 * Clase dónde gestionaremos la batería del agente y su necesidad de hacer parada para recargar.  
 * @author Miguel Keane
 */
public class Fuel {
    double fuel;
    
    /**
     *  @author María del Mar García Cabello
     * @param object
     */
    public void FuelParser(JsonObject object)
    {
        //Parseamos los datos de JSON
        //Obtenemos la cantidad de fuel que nos queda
        fuel= object.get("perceptions").asObject().get("fuel").asDouble();
    }
    
     /**
     *  @author María del Mar García Cabello
     *  Hacemos recarga del fuel
     */
    public void refuel(){
        fuel=100;
    }
    /**
     *  @author María del Mar García Cabello
     *  Gastamos 0.5% de fuel 
     */
    public void useFuel(){
        fuel=fuel-0.5;
    }
    /**
     *  @author María del Mar García Cabello
     *  @return fuel
     */
    public double getFuel(){
        return fuel;
    }
}
