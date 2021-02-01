import java.util.Scanner;

import fr.dgac.ivy.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;

public class Server {

	static Ivy ivy = new Ivy("Server", "Server Ready", null);	
	private static String addr = "127.255.255.255";
	private static FileWriter file;

	//static ServerVue serv = new ServerVue();
	
	public static void main(String... args) throws IvyException, IOException{
		ivy.start(addr);
		ivy.bindMsg("^Agent = (.*)", (sender, s)->msgReceived(sender, s));
		System.out.println("Server ready");
		file = new FileWriter("aggregators.json");
		Scanner keyboard = new Scanner(System.in);

		while(true){
			String text= keyboard.nextLine();

			switch(text){
				case "sensors" :
					requestSensors();
					break;
				case "aggregs" :
					requestInfosAggreg();
					break;
				case "stop" :
					System.exit(1);
					break;
				default :
					break;
			}	
		}
	}

	private static void requestSensors(){
		System.out.println("Requesting sensors");
        String command = "Server = request sensors";
        try {
			ivy.sendMsg(command);
		} catch (IvyException e) {
			e.printStackTrace();
		}
    }

    private static void requestInfosAggreg(){
		System.out.println("Requesting aggregators");
        String command = "Server = request aggregs";
        try {
			ivy.sendMsg(command);
		} catch (IvyException e) {
			e.printStackTrace();
		}
    }
	
	private static void msgReceived(IvyClient sender, String... args){
		System.out.println("msg received : " +args[0]);
		if(args[0].contains("Ready"))return;

		if(args[0].contains("Capteur")){
			System.out.println("Received a message from captors");
			String toSplit = args[0];
			String[] splittedString = toSplit.split("Capteur = ");
			String[] captorValues = new String[splittedString.length-1];
			for(int i = 1; i<splittedString.length;i++){
				captorValues[i-1] = splittedString[i];
			}
		}
		if(args[0].contains("Aggregateur")){
			// Definitely need a JSON parser
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
	
	private static void writeFile(JSONArray array){
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
