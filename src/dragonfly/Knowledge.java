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
    
    
    // Cómo vamos a guardar los resultados? Es de las decisiones más importantes. Sabéis CSV?? Así podemos guardar los resultados de los mapas
    // hacer exploraciones, ir guardando el mapa y lo que hacemos es poner al agente en 2 modos. Primero en exploración, mapeamos todo lo posible
    // Y guardamos los resultados de una iteración a otra. Luego otro con un algoritmo A* o lo que sea que llegue directo al destino, haciendo uso 
    // todo el conocimiento que tenga guardado. 

    
    Knowledge(String myMap) {
        mapID = myMap;
        // Load(mapID)  o algo similar sería lo suyo
        
    }

    Knowledge() {
        mapID="";
    }
}
