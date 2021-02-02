import java.util.Scanner;

import javax.swing.JButton;

import fr.dgac.ivy.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server {

	private Ivy ivy = new Ivy("Server", "Server Ready", null);	
	private String addr = "127.255.255.255";
	private FileWriter file;
	private ServerVue sv;
	
	public static void main(String... args) throws IvyException, IOException{
		Scanner keyboard = new Scanner(System.in);
		Server s = new Server();

		while(true){
			String text= keyboard.nextLine();

			switch(text){
				case "sensors" :
					s.requestSensors();
					break;
				case "aggregs" :
					s.requestInfosAggreg();
					break;
				case "stop" :
					keyboard.close();
					System.exit(1);
					break;
				default :
					break;
			}	
		}
	}

	public Server() throws IvyException, IOException {
		this.ivy.start(addr);
		ivy.bindMsg("^Agent = (.*)", (sender, s)->msgReceived(sender, s));
		System.out.println("Server ready");
		file = new FileWriter("aggregators.json");

		sv = new ServerVue();
		JButton but = sv.getPortailButton();
		but.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (but.getText().equals("Ouvrir")) {
					System.out.println("Ouverture");
					//TODO send ouvrir:1 to agent
					but.setEnabled(true);
				} else {
					//TODO send ouvrir:0 to agent
					System.out.println("Fermeture");
					but.setEnabled(true);
				}
			}
		});
	}

	private void requestSensors(){
		System.out.println("Requesting sensors");
        String command = "Server = request sensors";
        try {
			ivy.sendMsg(command);
		} catch (IvyException e) {
			e.printStackTrace();
		}
    }

    private void requestInfosAggreg(){
		System.out.println("Requesting aggregators");
        String command = "Server = request aggregs";
        try {
			ivy.sendMsg(command);
		} catch (IvyException e) {
			e.printStackTrace();
		}
    }
	
	private void msgReceived(IvyClient sender, String... args){
		System.out.println("msg received : " +args[0]);
		if(args[0].contains("Ready"))return;
		// Si le message contient "Capteur", on set les valeurs des capteurs dans la ServerVue
		if(args[0].contains("Capteur")){
			System.out.println("Received a message from captors");
			String toSplit = args[0];
			String[] splittedString = toSplit.split("Capteur = ");
			Integer[] captorValues = new Integer[splittedString.length-1];
			for(int i = 1; i<splittedString.length;i++){
				captorValues[i-1] = Integer.parseInt(splittedString[i]);
			}
			sv.setHygroValue(captorValues[0]);
			sv.setTempValue(captorValues[1]);
			sv.setPortailStateValue(captorValues[2]);
		}
		// Si le message contient "Aggregateur", on écrit les valeurs dans un fichier JSON
		if(args[0].contains("Aggregateur")){
			System.out.println("Received a message from captors");
			String toSplit = args[0];
			String[] splittedString = toSplit.split("Aggregateur = ");
			String[] aggreg = new String[splittedString.length-1];
			for(int i = 1; i<splittedString.length;i++){
				aggreg[i-1] = splittedString[i];
			}
			try {
				JSONArray array = new JSONArray(aggreg[0]);
				writeFile(array);

			} catch (JSONException je) {
				je.printStackTrace();
			}	
		}
	}
	
	private void writeFile(JSONArray array){
		for(int i=0; i < array.length(); i++){  
			try{
				JSONObject object = array.getJSONObject(i);
				file.write(object.toString());  
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException je){
				je.printStackTrace();
			} finally {
				try {
					file.flush();
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
	}
}
