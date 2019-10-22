/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * Clase principal del Agente DragonFly. Aquí estará especificada la heurística y hará uso de las demás clases para desenvolverse y alcanzar su objetivo
 * 
 *
 * @author Miguel Keane
 */
public class DragonFly extends SingleAgent{

    // Clases que usará el Agente. NOTA/ TODO: Posiblemente faltan clases, Magnetic, Gonio, etc... Id añadiendolas
    Fuel myFuel;
    GPS myGPS;
    Radar myRadar;
    Knowledge myKnowledge;
    
    //Posibles estados del agente:
    
    private final int NOLOG=0, LOGIN=1, LISTENING=2, END=3;
    
    
    private ACLMessage inbox, outbox; 
    
    private boolean found; 
    private int state;
    private String myMap;
    private AgentID action; 
    
    private String key;
    private AgentID myAgent;

    /**
     *
     * @param aid
     * @throws Exception
     */
    public DragonFly(AgentID aid) throws Exception {
        super(aid);
    }
    
    /**
     * 
     * Método constructor del Agente, inicializa ciertas variables más abajo especificadas y carga el 
     * conocimiento almacenado del mapa que se va a usar
     * 
     * @author Miguel Keane
     * 
     * @param agentID id del agente manejado por Magentix
     * @param map mapa que se va a ejecutar
     * @param virtualhost Recibirá los mensajes ACL del servidor 
     * @throws java.lang.Exception 
     * 
     * 
     */
    public DragonFly(AgentID agentID, String map, String virtualhost) throws Exception{
        super (agentID);
        myMap = map;
        myAgent = new AgentID(virtualhost);
        
        myKnowledge = new Knowledge(myMap);  // Hay que decidir cómo vamos a hacer la memoria. Sabéis trabajar CSV en java? yo lo he usado en Python, pero podría ser una buena solución, de esa forma inicializamos el mapa X, lo cargamos y tenemos ya guardada la información de anteriores ejecuciones. 
        
    }
    
    /**
     * @author miguelkeane
     * 
     * 
     */
    @Override
    public void init(){
        myFuel = new Fuel();
        myGPS = new GPS();
        myRadar = new Radar();
        myKnowledge = new Knowledge();
        inbox=null;
        outbox=null; 
        state=NOLOG;
        found=false;
        
        key="";
        
        
        
    }
   
    /**
     *
     */
    @Override
    public void start() {
        
    }
    
    
}
