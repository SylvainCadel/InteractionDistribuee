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
import java.util.ArrayList;

public class Agent {
    private Ivy busIvy;
    
    private FileReader reader;

    private JSONParser jsonParser;
    private JSONObject jsonObject;
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

            temperatureSensor = (JSONObject) jsonObject.get("Alaska");
            humiditySensor = (JSONObject) jsonObject.get("Bermudes");
            
            temperatureValue = (JSONObject) temperatureSensor.get("valeur");
            humidityValue = (JSONObject) humiditySensor.get("valeur");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch(ParseException e){
          e.printStackTrace();
        }
        
    }

    public void start() {
        try {
            busIvy.start("127.255.255.255:2010"); //<>//
            System.out.println("Agent started");

            busIvy.bindMsg("^Server = request (.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    // Si on reçoit request sensors on envoie les valeurs au serveur
                    if(args[0].contains("sensors")){
                        try {
                            sendToServerValue();                        
                        } catch (Exception e) {
                            System.out.println("Erreur message");
                            e.printStackTrace();
                        }
                    } 
                    // Si on reçoit request aggregs on envoie le json de l'aggregateur au serveur
                    if(args[0].contains("aggregs")){
                        try {
                            busIvy.sendMsg("Agent = Aggregateur = " +jsonObject.toString());                       
                        } catch (Exception e) {
                            System.out.println("Erreur message");
                            e.printStackTrace();
                        }
                    } 
                }
            });
            
            // TODO intégrer l'envoi de l'état du portail
            busIvy.bindMsg("^Server = (.*)", new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        sendToServerValue();                        
                    } catch (Exception e) {
                        System.out.println("Erreur message");
                        e.printStackTrace();
                    }
                }
            });
            
            if(second()%60 == 0)
              print("a");
              //sendToServerValue();
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        busIvy.stop();
    }

    public ArrayList<Integer> setJSONValue(){
        ArrayList<Integer> mean = new ArrayList<Integer>()
      
        int meanValueHygro = meanCalculation(getQueueHygro());
        humidityValue.put("valeur", meanValueHygro);
        mean.set(0, meanValueHygro);
        
        int meanValueTemp = meanCalculation(getQueueTemp());
        temperatureValue.put("valeur", meanValueTemp);
        mean.set(1, meanValueTemp);

        return mean;
    }

    public void sendToServerValue(){
        try {
            ArrayList<Integer> mean = setJSONValue();

            //TODO Concatener pour avoir qque chose de la forme "Agent = Capteur = x Capteur = y Capteur = z ..."
            busIvy.sendMsg("Agent = Capteur = " + mean.get(0));
            busIvy.sendMsg("Agent = Capteur = " + mean.get(1));
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public int meanCalculation(Queue<String> queueSensor){
      Object queueValue;
      int queueSize = queueSensor.size();
      int totalValue = 0;

      while ((queueValue = queueSensor.poll()) != null){
          totalValue += (int)queueValue;
      }

      int meanValue = totalValue / queueSize;
      return meanValue;
    }
}
