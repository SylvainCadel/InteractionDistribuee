import java.util.Scanner;

import fr.dgac.ivy.*;

public class Server {

	static Ivy ivy = new Ivy("Server", "Server Ready", null);	
	private static String addr = "127.255.255.255";
	

	public static void main(String... args) throws IvyException {
		ivy.start(addr);
		ivy.bindMsg("^Agent = (.*)", (sender, s)->msgReceived(sender, s));
		System.out.println("Server ready");
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
        if(args.length == 0) {
			System.out.println("Received void message. Aborting.");
			return;
		}
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

			for(int i=1; i<captorValues.length+1; i++){
				System.out.println("Capteur " + i + " : " + captorValues[i-1]);
			}
		}
		if(args[0].contains("Aggregateur")){
			// Might need a JSON parser
			System.out.println("Received a message from captors");
			String toSplit = args[0];
			String[] splittedString = toSplit.split("Capteur = ");
			String[] captorValues = new String[splittedString.length-1];
			for(int i = 1; i<splittedString.length;i++){
				captorValues[i-1] = splittedString[i];
			}

			for(int i=1; i<captorValues.length+1; i++){
				System.out.println("Capteur " + i + " : " + captorValues[i-1]);
			}
		}
    }
}
