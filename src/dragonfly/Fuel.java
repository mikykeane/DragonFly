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
        fuel= object.get("fuel").asObject().asDouble();
    }
    
     /**
     *  @author María del Mar García Cabello
     *
     */
    public void Recargar(){
        fuel=100;
    }
    /**
     *  @author María del Mar García Cabello
     *
     */
    public void Moverse(){
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
