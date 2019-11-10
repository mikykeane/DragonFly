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
        // NO HACER PUSH. DATOS PRIVADOS. METERLOS EN .gitignore después del primer push
        String virtualhost = parser.nextLine();
        String username = parser.nextLine(); 
        String pass = parser.nextLine(); 
        //////////////////////////////////////////////////////////////////////
        System.out.println(virtualhost);
        System.out.println(username);
        System.out.println(pass);
        String map = "map8"; 


        AgentsConnection.connect("isg2.ugr.es",6000, virtualhost, username, pass, false);
        

        try {                
                dragonFly = new DragonFly(new AgentID("DronMejorado2"), map, virtualhost, username, pass);
           
                dragonFly.start();
                
        } catch (Exception ex) {
                System.err.println("Error creando el agente");
                System.exit(1);
        }
        
    }
}
