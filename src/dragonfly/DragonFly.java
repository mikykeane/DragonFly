/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    Scanner myRadar;
    Knowledge myKnowledge;
    Gonio myGonio;
    
    String myDirection;
    
    //Posibles estados del agente:
    
    private final int NOLOG=0, LOGIN=1, THINKING=2, LISTENING=3, END=4;
    
    
    
    private ACLMessage inbox, outbox; 
    
    private boolean end; 
    private int state;
    private String myMap;
    private String action; 
    
    private String myUser;
    private String myPass; 
    
    private String key;
    private AgentID myServer;
    

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
    public DragonFly(AgentID agentID, String map, String virtualhost, String user, String pass) throws Exception{
        super (agentID);
        myMap = map;
        myServer = new AgentID("Nahn");
        
        myUser=user;
        myPass=pass;
        myKnowledge = new Knowledge(myMap);  // Hay que decidir cómo vamos a hacer la memoria. Sabéis trabajar CSV en java? yo lo he usado en Python, pero podría ser una buena solución, de esa forma inicializamos el mapa X, lo cargamos y tenemos ya guardada la información de anteriores ejecuciones. 
        
    }
    
    /**
     * @author Miguel Keane
     * 
     * 
     */
    @Override
    public void init(){
        myFuel = new Fuel();
        myGPS = new GPS();
        myRadar = new Scanner();
        myKnowledge = new Knowledge();
        inbox=null;
        outbox=null; 
        state=NOLOG;
        end=false;
        
        key="";
        
    }
    
    /**
     * Método para gestionar los posibles estados del agente, indicando que debe hacer en cada etapa.
     * 
     * @author Miguel Keane Cañizares y Mar Garcia Cabello
     */
    @Override
    public void execute(){
        JsonObject parser = new JsonObject();
        
        while(!end){
            switch(state){
                case NOLOG:
                    login();
                    state=LOGIN;
                    break;
                case LOGIN:
                    System.out.println("Agent "+this.getName()+" is waiting for login response");
                    
                    receiveMessage();
                    break;
                case THINKING:
                    think();
                    move();
                case END:
                    logout();
                    end=true;
            }
            
        }
        
        
    }
   
   
    
    /**
    * Método para hacer login con el servidor
    * 
    * @author Miguel Keane Cañizares
     * @param user
     * @param password
    * 
    */
    public void login(){
        JsonObject parser = new JsonObject();
        try{
            parser.add("command", "login");
            parser.add("map",  myMap);
            parser.add("radar", true);
            parser.add("elevation", true);
            parser.add("magnetic",true);
            parser.add("gps", true);
            parser.add("fuel", true);
            parser.add("gonio", true);
            parser.add("user",this.myUser);
            parser.add("password",this.myPass);
        }catch( Exception e){
            System.err.println("Fallo enviando la señal de login al servidor");
        }
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(myServer);
        outbox.setContent(parser.toString());
        System.out.println(outbox);
        this.send(outbox);
    }
    
     public void logout(){
        JsonObject parser = new JsonObject();
        try{
            parser.add("command", "logout");
            parser.add("key",this.key);
        }catch( Exception e){
            System.err.println("Fallo enviando la señal de logout al servidor");
        }
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(myServer);
        outbox.setContent(parser.toString());
        System.out.println(outbox);
     }
     
     public void move(){
        JsonObject parser = new JsonObject();
        try{
            parser.add("command", myDirection);
            parser.add("key",this.key);
        }catch( Exception e){
            System.err.println("Fallo enviando la señal de movimiento al servidor");
        }
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(myServer);
        outbox.setContent(parser.toString());
        System.out.println(outbox);
     }

    
    /**
    * Método para gestionar las respuestas del servidor
    * 
    * @author Miguel Keane Cañizares
    * 
    */
    private void receiveMessage() {
        try{
            inbox= receiveACLMessage();
            
            JsonObject parser = Json.parse(inbox.getContent()).asObject();
            
            if (parser.get("result") != null){
                resultManagement(parser);
            }
            else if(parser.get("gonio") != null) {
                myGonio.GonioParser(parser);
            }
            else if (parser.get("radar")!= null){
                myRadar.ScannerParser(parser);
            }
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(DragonFly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
    * Método para gestionar los resultado en JSON del servidor
    * 
    * @author Miguel Keane Cañizares
    * @param parser JsonObject que contiene la información que se desea parsear
    * 
    */
    private void resultManagement(JsonObject parser)
    {
        String result= parser.get("result").asString().toUpperCase();
        switch (result){
            case "OK":
                if (parser.get("in-reply-to").asString().equals("login")){
                    state=THINKING;
                    key=parser.get("key").asString();
                    System.out.println("Recibido OK a login");
                }
                               
                break;
            case "CRASHED":
                state=END;
                System.err.println("El agente "+ this.getName()+ " se ha estrellado. Misión fracasada. Mejore la heurística");
                break;
            case "BAD_COMMAND":
                state=END;
                System.err.println("Error: Accion no reconocida");
                break;
            case "BAD_KEY":
                state=END;
                System.err.println("Error: La llave introducida es incorrecta.");
                break;
            case "BAD_MAP":
                state=END;
                System.err.println("Error: El mapa introducido no es válido. Corrija. ");
                break;
               
        }
    }
    
    
    private void think(){
        
        myDirection="moveNW";
        
    }
    
}
