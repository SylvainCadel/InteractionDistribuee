import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerVue extends JFrame {

	private BufferedImage img = ImageIO.read(new File("maison.png"));

	private JLabel label = new JLabel("Hygrometrie : 100%");
	private JLabel labelTemp = new JLabel("T° : 25°C");
	private JLabel labelPort = new JLabel("Portail : ");
	private JLabel labelPortStatue = new JLabel("Fermé");

	JButton but = new JButton("Ouvrir");
	JButton butRefresh = new JButton("Refresh");

	public ServerVue() throws IOException {
		super("My Garden");
		this.setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(img, 0, 0, null);
			}
		});
		this.getContentPane().setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(img.getWidth(), img.getHeight());
		this.setResizable(false);
		setVisible(true);

		JPanel panel = new JPanel();
		label.setForeground(Color.BLUE);
		panel.add(label);
		panel.setOpaque(false);
		panel.setBounds(150, 80, 200, 200);
		this.add(panel);

		JPanel panelTemp = new JPanel();
		labelTemp.setForeground(Color.RED);
		panelTemp.add(labelTemp);
		panelTemp.setOpaque(false);
		panelTemp.setBounds(50, 400, 200, 200);
		this.add(panelTemp);

		JPanel panelPort = new JPanel();
		setPortailStateValue(1);
		labelPort.setForeground(Color.BLACK);
		panelPort.add(labelPort);
		panelPort.add(labelPortStatue);
		panelPort.setOpaque(false);
		panelPort.setBounds(350, 650, 200, 100);
		this.add(panelPort);

		
		but.setBounds(410, 625, 85, 25);
		this.add(but);

		butRefresh.setBounds(10, 650, 85, 25);
		this.add(butRefresh);

		this.revalidate();
	}

	public JButton getPortailButton() {
		return this.but;
	}

	public JButton getRefreshButton() {
		return this.butRefresh;
	}
	
	public void setHygroValue(int val) {
		this.label.setText("Humidité : " + val + "%");
	}

	public void setTempValue(int val) {
		this.labelTemp.setText("T° : " + val + "°C");
	}

	public void setPortailStateValue(int val) {
		switch(val) {
			case 1 :
				this.labelPortStatue.setText("Fermé");
				this.labelPortStatue.setForeground(Color.RED);
				break;

			case 2 : 
				this.labelPortStatue.setText("Ouverture");
				this.labelPortStatue.setForeground(Color.YELLOW);
				break;

			case 3 : 
				this.labelPortStatue.setText("Ouvert");
				this.labelPortStatue.setForeground(Color.BLUE);
				break;

			case 4 : 
				this.labelPortStatue.setText("Fermeture");
				this.labelPortStatue.setForeground(Color.YELLOW);
				break;

			default :
				this.labelPortStatue.setText("Fermé");
				this.labelPortStatue.setForeground(Color.RED);
				break;
		}
	}

	public static void main(String[] args) {
		try {
			ServerVue sv = new ServerVue();
			Thread.sleep(1000);
			sv.setPortailStateValue(2);
			Thread.sleep(1000);
			sv.setPortailStateValue(3);
			Thread.sleep(1000);
			sv.setPortailStateValue(4);
			Thread.sleep(1000);
			sv.setPortailStateValue(5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
