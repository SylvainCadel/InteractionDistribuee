import java.util.Scanner;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException; 

public class Server {
    static Ivy ivy = new Ivy("Server", "Server Ready", null);
    private static String addr ="127.255.255.255";

    public static void main(String... args) throws IvyException {
		ivy.start(addr);
		ivy.bindMsg("^Agent = (.*)", (sender, s)->msgReceived(sender, s));
		System.out.println("Server Ready");

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
			String[] splittedString = toSplit.split(" ");
			
			for(String part : splittedString){
				System.out.println(part);
			}
		}
		if(args[0].contains("Aggregateur")){
			System.out.println("Received a message from aggregators");
			String toSplit = args[0];
			String[] splittedString = toSplit.split(" ");
			
			for(String part : splittedString){
				System.out.println(part);
			}
		}
    }
}