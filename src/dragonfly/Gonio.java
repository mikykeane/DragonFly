/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * Clase que gestiona la distancia y la dirección a la que se encuentra el objetivo
 * @author miguelkeane
 */
public class Gonio {
    //Distancia a la casilla objetivo en numero de celdas
    int distance; 
    //Posicion de la casilla objetivo en angulos
    double angle; 
    //Aqui guardaremos los datos de los angulos
    HashMap<String, Integer> angulos; 
 
        /**
     * @author María del Mar García Cabello
     * @param object
     */
    public void GonioParser(JsonObject object){
           angulos=new HashMap<>();
            //Rellenamos el vector con los grados de los angulos
           angulos.put("Norte",0);
           angulos.put("NorEste",45);
           angulos.put("Este",90);
           angulos.put("SurEste",135);
           angulos.put("Sur",180);
           angulos.put("SurOeste",225);
           angulos.put("Este",270);
           angulos.put("NorOeste",315);
        
        //Obtenemos la distancia en numero de celdas al objetivo 
        distance= object.get("distance").asObject().asInt();
        //Obtenemos la dirección al que está el objetivo(en angulos)
        angle = object.get("angle").asObject().asDouble();
        
        int minimo = 360;
        int aux= Math.abs(angle - /*Aqui van cada una de las direcciones, N,S,E,W,NE,NW,SE,SW*/) % 360;
        int distancia;
        if(aux>180)
            distancia=360-aux;
        else
            distancia=aux;
        
        //Vamos a comparar la dirección del objetivo con las direcciones a las que nos podemos mover
        //para encontrar la más cercana
        for(int i=0; i<8;i++){
            
        }
        
        
       /* 
          public static int distance(int alpha, int beta) {
        int phi = Math.abs(beta - alpha) % 360;       // This is either the distance or 360 - distance
        int distance = phi > 180 ? 360 - phi : phi;
        return distance;
        
        */
        
    }
        
    }
}
