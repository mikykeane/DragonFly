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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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
    Scanner myScanner;
    Knowledge myKnowledge;
    Gonio myGonio;
    
    String myDirection;
    
    //Posibles estados del agente:
    private final int NOLOG=0, LOGIN=1, THINKING=2, LISTENING=3, END=4;
    
    
    
    private ACLMessage inbox, outbox; 
    
    private boolean end;     
    private boolean isGoal=false;
    private int state;
    private String myMap;
    private String action; 
    
    private String myUser;
    private String myPass; 
    
    private String key;
    private AgentID myServer;
    
    private String previousMove;
    

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
     * @param user 
     * @param pass 
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
        myScanner = new Scanner();
        myKnowledge = new Knowledge();
        myGonio = new Gonio();
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
       // JsonObject parser = new JsonObject();
        
        while(!end){
            switch(state){
                case NOLOG:
                    login();
                    //state=LOGIN;
                    break;
               //7 case LOGIN:
                  //  System.out.println("Agent "+this.getName()+" is waiting for login response"); 
                    //receiveMessage();
                    //break;
                case THINKING:
                    //receiveMessage();
                    think();
                    //if (end==false)
                       // move();
                    //break;
                case LISTENING:
                    for(int i = 0; i < 2; i++){
                        if (state != END)
                            receiveMessage();
                    }
                break;
                case END:
                    logout();
                    end=true;
                    break;
            }
            
        }
              
    }
   
   
    
    /**
    * Método para hacer login con el servidor
    * 
    * @author Miguel Keane Cañizares
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
        System.out.println("Agent "+this.getName()+" is waiting for login response"); 
        state=LISTENING;
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
        this.send(outbox);
        state=LISTENING;
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
        this.send(outbox);
        System.out.println(outbox);
        state=LISTENING;
        
     }

    
    /**
    * Método para gestionar las respuestas del servidor
    * 
    * @author Miguel Keane Cañizares, María del Mar García Cabello
    * 
    */
    private void receiveMessage() {
        try{
            //Recivimos un mensaje del servidor
            inbox= receiveACLMessage();
            //Lo pasamos a un JSon para poder trabajar con el
            JsonObject parser = Json.parse(inbox.getContent()).asObject();
            
            
            //Si es resultado, vamos a comprobar el estado en resultManagement
            if (parser.get("result") != null){
                System.out.println(inbox.getContent());
                resultManagement(parser);
            }else if (parser.get("perceptions") != null){
                state=THINKING;
                    myGonio.GonioParser(parser);
                //Si es radar,magnetic o elevatio, lo gestionamos en scanner
               
                    myScanner.ScannerParser(parser);
                //En el caso de que sea GPS, lo gestionamos en GPS
                
                myGPS.GPSParser(parser);
                //En el caso de que sea Fuel, lo gestionamos en Fuel
                
                myFuel.FuelParser(parser);
                //En el caso de que sea goal. MIRAR ESTO MAS DETENIDAMENTE
                
                    goalParser(parser);
                //En el caso de que sea status, 
                if(parser.get("status") != null) {
                    myFuel.FuelParser(parser);
                }

            }if (parser.get("trace")!=null){
                System.out.println("Recibiendo traza");
                JsonArray ja = parser.get("trace").asArray();
                byte data[] = new byte [ja.size()];
                for(int i=0; i<data.length; i++){
                    data[i] = (byte) ja.get(i).asInt();
                }
                FileOutputStream fos = new FileOutputStream(myMap+".png");
                fos.write(data);
                fos.close();
                System.out.println("Traza Guardada");
                end=true;
            }        
               
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(DragonFly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void goalParser(JsonObject parser){
        isGoal =parser.get("perceptions").asObject().get("goal").asBoolean();    
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
                //CREO QUE CRASH NO DEBERIA ESTAR EN LAS RESPUESTAS DE result
            case "CRASHED":
                state=END;
                System.err.println("El agente "+ this.getName()+ " se ha estrellado. Misión fracasada. Mejore la heurística");
                break;
            case "BAD COMMAND":
                state=END;
                System.err.println("Error: Accion no reconocida");
                break;
            case "BAD KEY":
                state=END;
                System.err.println("Error: La llave introducida es incorrecta.");
                break;
            case "BAD MAP":
                state=END;
                System.err.println("Error: El mapa introducido no es válido. Corrija. ");
                break;
               
        }
    }
    
    
    private void think(){
        if (myFuel.getFuel() <=10){
            if (myScanner.elevation[5][5]>0){
                myDirection="moveDW";
                move();
                //myFuel.useFuel();
            }else {
                //myFuel.refuel();
                myDirection="refuel";
                move();
                System.out.println("Hacemos refuel, por que quedaba: " + myFuel.getFuel());
            }
        } 
        else if (myScanner.magnetic[5][5]==1){
            if (myScanner.elevation[5][5]>0){
                myDirection="moveDW";
                move();
                //myFuel.useFuel();
            }else
                state=END;
        }else{
        String movimiento; 
        movimiento = decideAngle();
        System.out.println("Quiere ir a: " + movimiento);
        if (alturaPosible(movimiento)){
            myDirection=movimiento;
        }else if (myScanner.radar[5][5]<180){
            myDirection="moveUP";
        }

        System.out.println("Finalmente se mueve a: " + myDirection);

            previousMove=myDirection;
            //Realizamos el movimiento
            move();
            //myFuel.useFuel();
            
        }  
        
    }
    
     public boolean casillaDisponible(String dir){
        boolean disponible=true;
        if (null != dir)switch (dir) {
            case "moveN":
                if (myScanner.radar[4][5] == 0 || myScanner.radar[4][5]>180)
                    disponible=false;
                break;
            case "moveNW":
                if (myScanner.radar[4][4] == 0 || myScanner.radar[4][4]>180)
                    disponible=false;
                break;
            case "moveNE":
                if (myScanner.radar[4][6] == 0 || myScanner.radar[4][6]>180)
                    disponible=false;
                break;
            case "moveE":
                if (myScanner.radar[5][6] == 0 || myScanner.radar[5][6]>180)
                    disponible=false;
                break;
            case "moveSE":
                if (myScanner.radar[6][6] == 0 || myScanner.radar[6][6]>180)
                    disponible=false;
                break;
            case "moveS":
                if (myScanner.radar[6][5] == 0 || myScanner.radar[6][5]>180)
                    disponible=false;
                break;
            case "moveSW":
                if (myScanner.radar[6][4] == 0 || myScanner.radar[6][4]>180)
                    disponible=false;
                break;
            case "moveW":
                if (myScanner.radar[5][4] == 0 || myScanner.radar[5][4]>180)
                    disponible=false;
                break;
            default:
                break;
        }
        return disponible;
    }
    
     public String decideAngle(){
          HashMap<Integer, String> angulos;
            // Decidimos ángulo
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
            anguloActual = Math.abs(myGonio.angle-i)%360; 
            if(anguloActual>180){
                distancia=360-anguloActual;
            }else distancia=anguloActual;
            
            if (previousMove == oppositeAngle(angulos.get(i))){
                distancia += 180;
            }
            
            //Nos quedamos con la distancia mas pequeña
            if (casillaDisponible(angulos.get(i))){
                if (iWasHereBefore(angulos.get(i))){
                    distancia += 360;
                }
                if(distanciaMinima>distancia) {
                    distanciaMinima=distancia;
                    movimiento=angulos.get(i);
                }
            }
            
                
        } 
        return movimiento; 
     }
     
     public boolean iWasHereBefore(String dir){
         boolean iWas = false;
         if (null != dir)switch (dir) {
            case "moveN":
                if(myGPS.beenHere[myGPS.x-1][myGPS.y]){
                    iWas=true;
                }
                break;
            case "moveNW":
                 if(myGPS.beenHere[myGPS.x-1][myGPS.y-1]){
                    iWas=true;
                }
                break;
            case "moveNE":
                if(myGPS.beenHere[myGPS.x-1][myGPS.y+1]){
                    iWas=true;
                }
                break;
            case "moveE":
                 if(myGPS.beenHere[myGPS.x][myGPS.y+1]){
                    iWas=true;
                }
                break;
            case "moveSE":
                 if(myGPS.beenHere[myGPS.x+1][myGPS.y+1]){
                    iWas=true;
                }
                break;
            case "moveS":
                 if(myGPS.beenHere[myGPS.x+1][myGPS.y]){
                    iWas=true;
                }
                break;
            case "moveSW":
                 if(myGPS.beenHere[myGPS.x+1][myGPS.y-1]){
                    iWas=true;
                }
                break;
            case "moveW":
                if(myGPS.beenHere[myGPS.x][myGPS.y-1]){
                    iWas=true;
                }
                break;
            default:
                break;
        }
         return iWas;
     }
     
    public String oppositeAngle(String dir){
        String opdir= new String(); 
        if (null != dir)switch (dir) {
            case "moveN":
                opdir = "moveS";
                break;
            case "moveNW":
                opdir = "moveSE";
                break;
            case "moveNE":
               opdir = "moveSW";
                break;
            case "moveE":
                opdir = "moveW";
                break;
            case "moveSE":
                opdir = "moveNW";
                break;
            case "moveS":
                opdir = "moveN";
                break;
            case "moveSW":
                opdir = "moveNE";
                break;
            case "moveW":
                opdir = "moveE";
                break;
            default:
                break;
        }
        return opdir;
    }
    
     public boolean alturaPosible(String dir){
        boolean posible=true;
        if (null != dir)switch (dir) {
            case "moveN":
                if (myScanner.elevation[4][5] < 0)
                    posible=false;
                break;
            case "moveNW":
                if (myScanner.elevation[4][4] < 0)
                    posible=false;
                break;
            case "moveNE":
                if (myScanner.elevation[4][6] < 0)
                    posible=false;
                break;
            case "moveE":
                if (myScanner.elevation[5][6] < 0)
                    posible=false;
                break;
            case "moveSE":
                if (myScanner.elevation[6][6] < 0)
                    posible=false;
                break;
            case "moveS":
                if (myScanner.elevation[6][5] < 0)
                    posible=false;
                break;
            case "moveSW":
                if (myScanner.elevation[6][4] < 0)
                    posible=false;
                break;
            case "moveW":
                if (myScanner.elevation[5][4] < 0)
                    posible=false;
                break;
            default:
                break;
        }
        return posible;
    }
    
}
