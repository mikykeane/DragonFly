/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Clase donde gestionaremos y haremos utilizable la percepción del mundo del agente
 * @author Miguel Keane
 */
public class Scanner {
    
    int radar[][]; // Tenemos que decidir como vamos a escánear los resultados. Falta mirar un poco mejor los guiones. 
    int magnetic[][];
    int elevation[][];
    /**
     * @author Miguel Keane 
     * @param object
     */
    public void ScannerParser(JsonObject object)
    {
        //Parseamos los datos de JSON
        radarParser(object);
        if (object.get("magnetic")!= null){ //¿Por que comprobamos si esta nulo? Si manda radar manda todos, creo
            magneticParser(object);
        }
        if (object.get("elevation")!= null){
            elevationParser(object);
        }       
        
    }
    /**
     * @author María del Mar García Cabello, Miguel Keane  
     * @param object
     */
    private void radarParser(JsonObject object){
        JsonArray toParse = object.get("radar").asArray();
        for (int i=0; i<11; i++){
            for(int j=0; j<11; j++){
                radar[i][j]= toParse.get(j+i).asInt();
            }
        }
    }
    /**
     * @author María del Mar García Cabello, Miguel Keane   
     * @param object
     */
    private void magneticParser(JsonObject object){
        JsonArray toParse = object.get("magnetic").asArray();
        for (int i=0; i<11; i++){
            for(int j=0; j<11; j++){
                magnetic[i][j]= toParse.get(j+i).asInt();
            }
        }
    }
    /**
     * @author María del Mar García Cabello, Miguel Keane  
     * @param object
     */  
    private void elevationParser(JsonObject object){
        JsonArray toParse = object.get("elevation").asArray();
        for (int i=0; i<11; i++){
            for(int j=0; j<11; j++){
                elevation[i][j]= toParse.get(j+i).asInt();
            }
        }
    }
    
    /**
     * La función devuelve el valor de magnetic en un punto. Si esa casilla es objetivo.
     * @author María del Mar García Cabello 
     * @param posx posicion x del mapa
     * @param posy posicion y del mapa
     * @return objetivo 0 si la celda no es objetivo, 1 si lo es. 
     * 
     */
    public int casillaObjetivo(int posx, int posy){
        //No se puede salir del mapa
        if(posx<11 && posy<11)
            return magnetic[posx][posy];
        else
            return -1;
    }
    
}

