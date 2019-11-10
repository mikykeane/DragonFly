/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;



/**
 * Clase Main donde gestionaremos la creación de agentes y la conexión con el servidor. 
 * 
 * @author Miguel Keane
 * 
 * 
 */
public class Main{
    
// Funciones para conectarse al servidor. EL resto lo maneja el agente DragonFly

    /**
     * @author Miguel Keane
     * @param args
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        DragonFly dragonFly;

        FileReader inputFile=null;
        try{
            inputFile = new FileReader("pass.txt");
            
        }catch(FileNotFoundException err){
            System.out.println("Error reading the password file");
        }
        Scanner parser = new Scanner(inputFile);
        
        ////////////////////////////////////////////////////////////////////
        //DATOS PRIVADOS.
        String virtualhost = parser.nextLine(); //Nombre del host
        String username = parser.nextLine(); //Nombre del grupo
        String pass = parser.nextLine();  //Contraseña del grupo
        //////////////////////////////////////////////////////////////////////
        System.out.println(virtualhost);
        System.out.println(username);
        System.out.println(pass);
        String map = "map4"; //Mapa que queremos que nuestro agente realice

        //Conectamos con el servidor
        AgentsConnection.connect("isg2.ugr.es",6000, virtualhost, username, pass, false);
        

        try {   //Creamos nuestro agente             
                dragonFly = new DragonFly(new AgentID("Dron1"), map, virtualhost, username, pass);
                dragonFly.start();
                
        } catch (Exception ex) {
                System.err.println("Error creando el agente");
                System.exit(1);
        }
        
    }
}
