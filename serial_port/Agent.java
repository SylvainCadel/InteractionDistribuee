package serial_port;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

public class Agent {
	private Ivy busIvy;

	private JSONObject jsonObject;
	private JSONObject humidityValue;
	private JSONObject temperatureValue;

	public Queue<Integer> listValHygro = new LinkedList<>();
	public Queue<Integer> listValTemp = new LinkedList<>();
	public String etat_portail;
	public String cmdPortail = "ouvre:0";

	private Callable<Integer> portailCallback;

	public Agent() {
		busIvy = new Ivy("Agent", null, null);

		try {

			JSONObject tempJSON = new JSONObject();
			tempJSON.put("valeur", 25);
			tempJSON.put("longitude", 50);
			tempJSON.put("latitude", 400);

			JSONObject hygroJSON = new JSONObject();
			hygroJSON.put("valeur", 50);
			hygroJSON.put("longitude", 150);
			hygroJSON.put("latitude", 80);

			jsonObject = new JSONObject();
			jsonObject.put("temp", tempJSON);
			jsonObject.put("hygro", hygroJSON);
	  
			humidityValue = jsonObject.getJSONObject("hygro");
			temperatureValue = jsonObject.getJSONObject("temp");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void start(Callable<Integer> callback) {
		this.portailCallback = callback;
		try {
			busIvy.start("127.255.255.255:2010"); // <>//

			busIvy.bindMsg("^Server = request (.*)", new IvyMessageListener() {
				public void receive(IvyClient client, String[] args) {
					// Si on reçoit request sensors on envoie les valeurs au serveur
					if (args[0].contains("portail ")) {
						try {
							cmdPortail = "ouvre:" + args[0].replace("portail ", "");
							portailCallback.call();
						} catch (Exception e) {
							System.out.println("Erreur message");
							e.printStackTrace();
						}
					}

					if (args[0].contains("capteur")) {
						try {
							sendToServerValue();
						} catch (Exception e) {
							System.out.println("Erreur message");
							e.printStackTrace();
						}
					}
				}
			});

			System.out.println("Agent started");
		} catch (IvyException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		busIvy.stop();
	}

	private void setJSONValue() {
		try {
			humidityValue.put("valeur", meanCalculation(this.listValHygro));
			temperatureValue.put("valeur", meanCalculation(this.listValTemp));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendToServerValue() {
		setJSONValue();
		try {
			busIvy.sendMsg(("Agent = capteurs = " + jsonObject.toString()).replaceAll("\n", ""));
		} catch (IvyException e) {
			e.printStackTrace();
		}
	}

	public void sendToServerPortail() {
		try {
			busIvy.sendMsg("Agent = portail = " + etat_portail);
		} catch (IvyException e) {
			e.printStackTrace();
		}
	}

	private int meanCalculation(Queue<Integer> queueSensor) {
		int queueSize = queueSensor.size();
		Integer[] queueValues = new Integer[queueSize];
		queueValues = queueSensor.toArray(queueValues);
		int totalValue = 0;
		for(Integer i : queueValues)
			totalValue += i;
		int meanValue = totalValue / queueSize;
		return meanValue;
	}


	public static void main(String[] args) {
		new Agent().start(null);
	}
}
