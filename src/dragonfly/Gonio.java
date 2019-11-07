/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.JsonObject;
import java.util.HashMap;

/**
 *
 * Clase que gestiona la distancia y la dirección a la que se encuentra el objetivo
 * @author miguelkeane, María del Mar García Cabello
 */
public class Gonio {
    //Distancia a la casilla objetivo en numero de celdas
    double distance; 
    //Posicion de la casilla objetivo en angulos
    double angle; 
    //Aqui guardaremos los datos de los angulos
    HashMap<Integer, String> angulos;   
    String move;
    
    /**
     * @author María del Mar García Cabello
     * @param object
     */
    public void GonioParser(JsonObject object){
           
        //Obtenemos la distancia en numero de celdas al objetivo 
        distance= object.get("distance").asObject().asDouble();
        System.out.println("La distancia es ");
        System.out.println(distance);
        //Obtenemos la dirección al que está el objetivo(en angulos)
        angle = object.get("angle").asObject().asDouble(); 
        /*
        //Obtenemos la distancia en numero de celdas al objetivo 
        distance= object.get("gonio").asObject().get("distance").asInt();
        //Obtenemos la dirección al que está el objetivo(en angulos)
        angle = object.get("perceptions").asObject().get("gonio").asObject().get("angle").asInt(); 
       
        */
    }
    
    /**
     * @author María del Mar García Cabello
     * @return move siguiente movimiento que tendremos que realizar
     *
     */
    public String objetivo(){
        angulos=new HashMap<>();
            //Rellenamos el vector con los grados de los angulos
           angulos.put(0,"moveN");
           angulos.put(45,"moveNE");
           angulos.put(90,"moveE");
           angulos.put(135,"moveSE");
           angulos.put(180,"moveS");
           angulos.put(225,"moveSW");
           angulos.put(270,"moveW");
           angulos.put(315,"moveNW");
    
        double anguloActual;
        //Distancia que hay entre el objetivo y cada uno de los angulos de movimiento posibles
        double distancia;
        //Distancia minima y hacia donde nos deberemos mover
        double distanciaMinima=360;
        String movimiento="moveN";//Por defecto nos moveremos al norte
        
        //Vamos a comparar la dirección del objetivo con las direcciones a las que nos podemos mover
        //para encontrar la más cercana
        
        //Recorremos el hasMap
        for(int i : angulos.keySet()) {
            //vemos que angulo nos conviene mas
            anguloActual = Math.abs(angle-i)%360; 
            if(anguloActual>180){
                distancia=360-anguloActual;
            }else distancia=anguloActual;
            
            //Nos quedamos con la distancia mas pequeña
            if(distanciaMinima>distancia) {
                distanciaMinima=distancia;
                movimiento=angulos.get(i);
            }
                
        } 
       move=movimiento; 
       System.out.println(move);
       return move;
    }
      
    
}
