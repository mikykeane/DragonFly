/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

/**
 * Clase donde se gestionará el conocimiento sobre los mapas del agente
 * @author Miguel Keane
 */
public class Knowledge {
    String mapID; 

    /**
     * @author Miguel Keane
     * @param myMap
     */    
    Knowledge(String myMap) {
        mapID = myMap;
        // Load(mapID)  o algo similar sería lo suyo
        
    }
    
    
    /**
     * @author Miguel Keane
     * 
     */
    Knowledge() {
        mapID="";
    }
}
