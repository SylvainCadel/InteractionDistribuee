import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
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
	
	BufferedImage img = ImageIO.read(new File("maison.png"));
	
   public ServerVue() throws IOException {
	   super("My Garden");
	   this.setContentPane(new JPanel() {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		    g.drawImage(img, 0, 0, null);
		}});
	   this.getContentPane().setLayout(null);
	   this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   this.setSize(img.getWidth(), img.getHeight());
	   this.setResizable(false);
	   setVisible(true);
	   
	   JPanel panel = new JPanel();
       JLabel label = new JLabel("Hygrometrie : 100%");
       label.setForeground(Color.BLUE);
       panel.add(label);
       panel.setOpaque(false);
       panel.setBounds(150, 80, 200, 200);
       this.add(panel);
       
       JPanel panelTemp = new JPanel();
       JLabel labelTemp = new JLabel("T° : 25°C");
       labelTemp.setForeground(Color.RED);
       panelTemp.add(labelTemp);
       panelTemp.setOpaque(false);
       panelTemp.setBounds(50, 400, 200, 200);
       this.add(panelTemp);
       
       JPanel panelPort = new JPanel();
       JLabel labelPort = new JLabel("Portail : ");
       JLabel labelPortStatue = new JLabel("Fermé");
       labelPortStatue.setForeground(Color.RED);
       labelPort.setForeground(Color.BLACK);
       panelPort.add(labelPort);
       panelPort.add(labelPortStatue);
       panelPort.setOpaque(false);
       panelPort.setBounds(350, 650, 200, 200);
       this.add(panelPort);
       
       JButton but = new JButton("Ouvrir");
       but.setBounds(410, 625, 85, 25);
       but.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(but.getText().equals("Ouvrir")) {
				but.setText("Fermer");
				labelPortStatue.setText("Ouvert");
				labelPortStatue.setForeground(Color.BLUE);
			} else {
				but.setText("Ouvrir");
				labelPortStatue.setText("Fermé");
				labelPortStatue.setForeground(Color.RED);
			}
		}
       });
       this.add(but);
   }
	
	public static void main(String[] args) {
		ServerVue sv;
		try {
			sv = new ServerVue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
