/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonfly;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;



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

        ////////////////////////////////////////////////////////////////////
        // NO HACER PUSH. DATOS PRIVADOS, METERLOS EN .gitignore después del primer push
        String virtualhost = "";
        String username = ""; 
        String pass = ""; 
        //////////////////////////////////////////////////////////////////////

        String map = "map1"; 

        AgentsConnection.connect("isg2.ugr.es",6000, virtualhost, username, pass, false);

        try {

                dragonFly = new DragonFly(new AgentID("dron1"), map, virtualhost);

                dragonFly.start();
                
        } catch (Exception ex) {
                System.err.println("Error creando el agente");
                System.exit(1);
        }
        
    }
}
