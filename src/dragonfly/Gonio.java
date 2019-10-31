/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.JsonObject;

/**
 *
 * Clase que gestiona la distancia y la dirección a la que se encuentra el objetivo
 * @author miguelkeane
 */
public class Gonio {
    int distance; 
    double angle; 
    
    public void GonioParser(JsonObject object){
        distance= object.get("distance").asObject().asInt();
        angle = object.get("angle").asObject().asDouble();
    }
}
