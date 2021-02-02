package serial_port;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.json.simple.JSONObject;
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

	private Callable<Void> portailCallback;

	public Agent() {
		busIvy = new Ivy("Agent", null, null);

		try {
			jsonObject = (JSONObject) (new JSONParser()).parse(new FileReader("capteur.json"));

			humidityValue = ((JSONObject) (((JSONObject) (((JSONObject) jsonObject.get("jardin")).get("hygro")))
					.get("valeur")));
			temperatureValue = ((JSONObject) (((JSONObject) (((JSONObject) jsonObject.get("jardin")).get("temp")))
					.get("valeur")));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void start(Callable<Void> callback) {
		this.portailCallback = callback;
		try {
			busIvy.start("127.255.255.255:2010"); // <>//

			busIvy.bindMsg("^Server = request (.*)", new IvyMessageListener() {
				public void receive(IvyClient client, String[] args) {
					// Si on reçoit request sensors on envoie les valeurs au serveur
					if (args[0].contains("portail")) {
						try {
							portailCallback.call();
						} catch (Exception e) {
							System.out.println("Erreur message");
							e.printStackTrace();
						}
					}
					// Si on reçoit request aggregs on envoie le json de l'aggregateur au serveur
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
		humidityValue.put("valeur", meanCalculation(this.listValHygro));
		temperatureValue.put("valeur", meanCalculation(this.listValTemp));
	}

	public void sendToServerValue() {
		setJSONValue();
		try {
			busIvy.sendMsg("Agent = " + jsonObject.toString());
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
}
