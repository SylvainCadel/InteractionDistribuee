import fr.dgac.ivy.*;

import java.awt.*;
import java.util.ArrayList;

public class Agent {
    private Ivy busIvy;
    private ArrayList<Integer> sensorValue;

    public Agent() {
        busIvy = new Ivy("Agent", null, null);
    }

    public void start() {
        try {
            busIvy.start("127.255.255.255:2010");
            System.out.println("Agent started");
            busIvy.bindMsg("^server request=(.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        sendToServer();/*args[0]);*/                        
                    } catch (Exception e) {
                        System.out.println("Erreur message");
                        e.printStackTrace();
                    }
                }
            });

        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        busIvy.stop();
    }

    public void sendToServer(){
        try {
            for(int i = 0; i < sensorValue.size(); i++){
                busIvy.sendMsg("Capteur" + i + " : " + sensorValue.get(i));
            }
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public int meanCalculation(){
        int totalValue = 0;
        for(int i = 0; i < sensorValue.size(); i++){
            totalValue = totalValue + sensorValue.get(i);
        }
        int meanValue = totalValue / sensorValue.size();
        return meanValue;
    }

    public static void main(String[] args) {
        Agent ag = new Agent();
        ag.start();
    
        Ivy busIvy = new Ivy("Test", null, null);
    
        //busIvy.start("127.255.255.255:2010");
        //busIvy.sendMsg("server request");
    
    }
}
