import fr.dgac.ivy.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Agent {
    private Ivy busIvy;
    private Queue<Integer> sensorValue;
    private JSONParser jsonParser;
    private JSONObject jsonObject;
    private FileReader reader;
    private JSONObject humiditySensor;
    private JSONObject temperatureSensor;
    private JSONObject humidityValue;
    private JSONObject temperatureValue;


    public Agent() {
        busIvy = new Ivy("Agent", null, null);

        try {
            reader = new FileReader("capteur.json");
            jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(reader);

            temperatureSensor = (JSONObject) jsonObject.get("temperature");
            humiditySensor = (JSONObject) jsonObject.get("humidite");
            
            temperatureValue = (JSONObject) jsonObject.get("valeur");
            humidityValue = (JSONObject) jsonObject.get("valeur");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        
    }

    public void start() {
        try {
            busIvy.start("127.255.255.255:2010");
            System.out.println("Agent started");

            busIvy.bindMsg("^server request=(.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        sendToServer();                        
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
        System.out.println("sensors info sent");
        try {
            int meanValue = meanCalculation();

            busIvy.sendMsg("Capteur : " + );
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public int meanCalculation(int id){
        Object queueValue;
        int queueSize = sensorValue.size();
        int totalValue = 0;

        while ((queueValue = sensorValue.poll()) != null){
            totalValue += (int)queueValue;
        }

        int meanValue = totalValue / queueSize;
        return meanValue;
    }

    public static void main(String[] args) {
        Agent ag = new Agent();
        ag.start();
    
        Ivy busIvy = new Ivy("Test", null, null);
        try {
            busIvy.start("127.255.255.255:2010");
            busIvy.sendMsg("server request");
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }
}
