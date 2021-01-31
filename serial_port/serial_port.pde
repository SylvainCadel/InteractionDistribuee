import processing.serial.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

Serial myPort1;
Serial myPort2;
String val;
Queue<String> listValHygro = new LinkedList<String>();
Queue<String> listValTemp = new LinkedList<String>();
String[] nums;

void setup()
{
  size(200,200);
  // COM7 sont les entrées et COM8 sont les sorties
  myPort1 = new Serial(this, "COM7", 9600);
  myPort2 = new Serial(this, "COM8", 9600);
  
}

void draw()
{
  //Reception de message
  if ( myPort1.available() > 0) {  // If data is available,
    val = myPort1.readStringUntil('\n');         // read it and store it in val
    nums = split(val, ' ');
  }
  listValHygro.add(nums[1]);
  
  listValTemp.add(nums[3]);
  
  //println(val); //print it out in the console
  
  ouverturePorte(0);
}

void ouverturePorte(int etatporte){
  myPort2.write(etatporte);          //envoie de l'état
}
