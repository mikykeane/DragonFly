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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal del Agente DragonFly. Aquí estará especificada la heurística y hará uso de las demás clases para desenvolverse y alcanzar su objetivo *
 * @author Miguel Keane
 * 
 */
public class DragonFly extends SingleAgent{

    // Clases que usará el Agente
    Fuel myFuel;
    GPS myGPS;
    Scanner myScanner;
    Knowledge myKnowledge;
    Gonio myGonio;
    
    String myDirection;
    
    //Estados del agente:
    private final int NOLOG=0, LOGIN=1, THINKING=2, LISTENING=3, END=4;
    
    private ACLMessage inbox, outbox; //para comunicarnos con el servidor
    private String key; //Clave
    private AgentID myServer; //Nombre de nuestro agente
    
    private boolean end;
    private int state;
    private String myMap; //Mapa en el que estamos
    
    private String myUser; //Usuario del grupo
    private String myPass; //Contraseña del grupo

    private String previousMove; //Movimiento anterior
    

    /**
     * @author Miguel Keane
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
        myKnowledge = new Knowledge(myMap);
        
    }
    
    /**
     * @author Miguel Keane
     * Inicializa todas las variables
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
     * @author Miguel Keane Cañizares y Maria del Mar Garcia Cabello
     */
    @Override
    public void execute(){
        //Mientras no termine el agente
        while(!end){
            switch(state){ //Podemos estar en diferentes estados
                case NOLOG: //No hemos hecho log aun
                    login(); //Nos logueamos
                    break;
                case THINKING://Pensando
                    think(); //Pensamos el siguiente movimiento o cosa que tiene que realizar el agente
                case LISTENING: //EScuchando mensajes del servidor
                    for(int i = 0; i < 2; i++){
                        if (state != END)
                            receiveMessage(); //Escuchamos el mensaje del servidor
                    }
                break;
                case END://Hemos terminado
                    logout(); //Nos deslogueamos
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
        this.send(outbox); //Hacemos el envio de la info de login al servidor
        System.out.println("Agent "+this.getName()+" is waiting for login response"); 
        state=LISTENING;//Una vez que ya nos hemos logueado, podemos escuchar los mensajes del servidor
    }
    
    /**
    * Método para hacer logout con el servidor
    * 
    * @author Miguel Keane Cañizares, María del Mar García Cabello
    * 
    */
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
        state=LISTENING;//Mantenemos el estado a escuando para que nos llegue el mensaje de que se ha deslogueado correctamente.
        System.out.println(outbox);
     }
     
     
    /**
    * Método para hacer que el agente se mueva por el mapa
    * 
    * @author Miguel Keane Cañizares
    * 
    */
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
            }else if (parser.get("perceptions") != null){ //Si ya tenemos las percepciones, es hora de pensar.
                state=THINKING;
                //Parseamos datos de Gonio
                myGonio.GonioParser(parser);
               //Parseamos los datos del scanner
                myScanner.ScannerParser(parser);
                //Parseamos los datos del GPS
                myGPS.GPSParser(parser);
                //Parseamos los datos del fuel
                myFuel.FuelParser(parser);
            }
            //Si en el mensaje vienen los datos de la traza
            if (parser.get("trace")!=null){
                System.out.println("Recibiendo traza");
                //Parseamos los datos de la traza
                JsonArray ja = parser.get("trace").asArray();
                byte data[] = new byte [ja.size()];
                for(int i=0; i<data.length; i++){
                    data[i] = (byte) ja.get(i).asInt();
                }
                //Los guardamos en una imagen
                FileOutputStream fos = new FileOutputStream(myMap+".png");
                fos.write(data);
                fos.close();
                System.out.println("Traza Guardada");
                end=true; //Ya hemos terminado con este mapa
            }        
               
        } catch (InterruptedException | IOException ex) {
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
        String result= parser.get("result").asString().toUpperCase();//Parseamos los datos
        switch (result){ //Comprobamos el mensaje
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
    
    
    /**
    * Método para pensar el siguiente movimiento del agente
    * 
    * @author Miguel Keane Cañizares
    * 
    */
    private void think(){
        if (myFuel.getFuel() <=10){//Si tenemos poco combustible
            if (myScanner.elevation[5][5]>0){ //Si no estamos en el suelo, bajamos
                myDirection="moveDW";
                move();
            }else {
                myDirection="refuel";//Si estamos en el suelo cargamos combustible
                move();
                System.out.println("Hacemos refuel, por que quedaba: " + myFuel.getFuel());
            }
        }else if (myScanner.magnetic[5][5]==1){ //Si estamos en una celda objetivo
            if (myScanner.elevation[5][5]>0){ //Y no estamos en el suelo, bajamos
                myDirection="moveDW";
                move();
            }else //Si estamos en el suelo, hemos terminado
                state=END;
        }else{//Si no estamos en una celda objetivo
        String movimiento; 
        movimiento = decideAngle(); //Decidimos donde tenemos que ir en el sigiente movimiento
        System.out.println("Quiere ir a: " + movimiento);
        if (alturaPosible(movimiento)){ //Si es posible movernos a esa celda porque la altura es la adecuada
            myDirection=movimiento; //Nos movemos a esa celda
        }else if (myScanner.radar[5][5]<180){ //Si no es posible
            myDirection="moveUP"; //Subimos
        }

        System.out.println("Finalmente se mueve a: " + myDirection);

        previousMove=myDirection;
        //Realizamos el movimiento
        move();
            
        }  
        
    }
    
    
    /**
    * Método para saber si es posible moverse a una casilla concreta
    * @author Miguel Keane Cañizares
    * @return disponible
    * @param dir Dirección a la que nos gustaría movernos
    * 
    */
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
    
    
    /**
    * Método para elegir el siguiente movimiento
    * @author Miguel Keane Cañizares, María del Mar García Cabello
    * @return movimiento Siguiente movimiento que debe realizar el agente
    * 
    */
     public String decideAngle(){
        HashMap<Integer, String> angulos=new HashMap<>();
          
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
