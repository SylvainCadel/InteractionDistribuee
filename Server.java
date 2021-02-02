import javax.swing.JButton;

import fr.dgac.ivy.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server {

	private Ivy ivy = new Ivy("Server", "Server Ready", null);
	private String addr = "127.255.255.255";
	private ServerVue sv;

	public static void main(String... args) throws IvyException, IOException {
		new Server();
	}

	public Server() throws IvyException, IOException {
		this.ivy.start(addr);
		ivy.bindMsg("^Agent = (.*)", (sender, s) -> msgReceived(sender, s));
		System.out.println("Server ready");

		sv = new ServerVue();
		JButton but = sv.getPortailButton();
		but.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (but.getText().equals("Ouvrir")) {
						System.out.println("Ouverture");
						ivy.sendMsg("Server = request portail 1");
						but.setEnabled(false);
					} else {
						System.out.println("Fermeture");
						ivy.sendMsg("Server = request portail 0");
						but.setEnabled(false);
					}
				} catch (IvyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		sv.getRefreshButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				requestSensors();
			}
		});
	}

	private void requestSensors() {
		System.out.println("Requesting sensors");
		String command = "Server = request capteur";
		try {
			ivy.sendMsg(command);
		} catch (IvyException e) {
			e.printStackTrace();
		}
	}

	private void msgReceived(IvyClient sender, String... args) {
		if(args.length > 0) {
			System.out.println("msg received : " + args[0]);
			// Si le message contient "Capteur", on set les valeurs des capteurs dans la
			// ServerVue
			if (args[0].contains("capteurs = ")) {
				System.out.println("Received a message from captors");
				String splittedString = args[0].replace("capteurs = ", "");

				// todo parseJson
				JSONParser jp = new JSONParser();
				try {
					JSONObject jo = (JSONObject) jp.parse(splittedString);

					sv.setHygroValue(Integer.parseInt(((JSONObject)jo.get("hygro")).get("valeur").toString()));
					sv.setTempValue(Integer.parseInt(((JSONObject)jo.get("temp")).get("valeur").toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// Si le message contient "Aggregateur", on écrit les valeurs dans un fichier JSON
			if(args[0].contains("portail = ")){
				System.out.println("Received a message from portail");
				int state = Integer.parseInt(args[0].replace("portail = ", ""));
				sv.setPortailStateValue(state);
				if(state == 1 || state == 3)
					sv.getPortailButton().setEnabled(true);
			}
		}
	}
}
