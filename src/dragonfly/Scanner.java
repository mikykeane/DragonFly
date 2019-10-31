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
        JsonArray toParse = object.get("radar").asArray();
        for (int i=0; i<11; i++){
            for(int j=0; j<11; j++){
                radar[i][j]= toParse.get(j+i).asInt();
            }
        }
        
        if (object.get("magnetic")!= null){
                toParse = object.get("magnetic").asArray();
            for (int i=0; i<11; i++){
               for(int j=0; j<11; j++){
                   radar[i][j]= toParse.get(j+i).asInt();
               }
            }
        }
        if (object.get("elevation")!= null){
             toParse = object.get("elevation").asArray();
            for (int i=0; i<11; i++){
                for(int j=0; j<11; j++){
                    radar[i][j]= toParse.get(j+i).asInt();
                }
            }
        }
        
    }
}
