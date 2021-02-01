import processing.serial.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

Serial myPort1;
Serial myPort2;
String val;
String val2;
Queue<String> listValHygro = new LinkedList<String>();
Queue<String> listValTemp = new LinkedList<String>();
String etat_portail;
String[] nums;
String[] nums2;
Agent ag;

void setup()
{
  ag = new Agent();
  // COM7 sont les entrées et COM8 sont les sorties
  myPort1 = new Serial(this, "COM3", 9600);
  myPort2 = new Serial(this, "COM6", 9600);
  this.ag.start();
  
}
 //<>//
void draw()
{
  if ( myPort1.available() > 0) {  // If data is available,
    val = myPort1.readStringUntil('\n');         // read it and store it in val
    nums = split(val,':');
    if(nums.length > 1){
      if(nums[0].equals("hygro")){
        listValHygro.add(nums[1]);
      }
      else if(nums[0].equals("temp")){
        listValTemp.add(nums[1]);
      } 
    }
  }
  delay(500); //<>//
  
  //println(val); //print it out in the console
  
  if(myPort2.available() > 0){
    val2 = myPort2.readStringUntil('\n');
    nums2 = split(val2,":");
    if(nums2.length > 1){
        etat_portail.equals(nums2[1]);
    }
  }
  
  
}

void ouverturePorte(String ouverture){
  if(etat_portail.equals("1") && ouverture.equals("0")){
    ouverture = "ouvrir:" + ouverture;
    myPort2.write(ouverture);          //envoie de l'état
    delay(500);                       //
  }
  else if(etat_portail.equals("0") && ouverture.equals("1")){
    ouverture = "ouvrir:" + ouverture;
    myPort2.write(ouverture);          //envoie de l'état
    delay(500);
  }
}

Queue<String> getQueueHygro(){
  return listValHygro;
}

Queue<String> getQueueTemp(){
    return listValTemp;
}
