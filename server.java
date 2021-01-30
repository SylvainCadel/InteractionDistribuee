import java.util.ArrayList;
import java.util.List;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException; 

public class Main {
    static Ivy ivy = new Ivy("Server", "Server Ready", null);
    private static final int MAX_INFO_AGE = 5000;
    private static String addr ="127.255.255.255";

    public static void main(String... args) throws IvyException {
		{//scope
			int i = 0;
			while(i < args.length) {
				switch(args[i]) {
				case "-addr":
					if(i + 1 < args.length) {
						i++;
						addr = args[i];
					}
					break;
				case "-h":
				case "-help":
					System.out.println("-addr (addresse) : selectionne l'addresse du bus ivy (d�faut : "+addr+")");
					System.out.println("-h : affiche cette aide");
					System.exit(0);
					break;
				}
				i++;
			}
		}
		ivy.start(addr);
		ivy.bindMsg("^(.*)", (sender, s)->msgReceived(sender, s));
		startConsole();
		System.out.println("Server Ready");
    }
    
    private static void requestSensors(){
        String command = "request sensors";
        try {
			ivy.sendMsg(command);
		} catch (IvyException e) {
			e.printStackTrace();
		}
    }

    private static void requestInfosAggreg(){
        String command = "request aggregs";
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
		System.out.println("msg received :" +args[0]);
		if(args[0].contains("Ready"))return;
        // TODO interpretation à faire
    }
}