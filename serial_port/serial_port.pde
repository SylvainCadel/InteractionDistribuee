import processing.serial.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import serial_port.*;

Serial myPort1;
Serial myPort2;
String val;
String val2;
String[] nums;
String[] nums2;
Agent ag;

int time = 0;

void setup()
{
  ag = new Agent();
  // COM7 sont les entrées et COM8 sont les sorties
  myPort1 = new Serial(this, "COM5", 9600);
  myPort2 = new Serial(this, "COM3", 9600);
  this.ag.start(new Callable<Integer>() {
    public Integer call() {
        return ouverturePorte();
    }
  });
  
}

void draw()
{
  if ( myPort1.available() > 0) {  // If data is available,
    val = myPort1.readStringUntil('\n');         // read it and store it in val
    nums = split(val,':');
    if(nums.length > 1){
      nums[1] = nums[1].replaceAll("\n", "");
      if(nums[0].equals("hygro")){
        ag.listValHygro.add(Integer.parseInt(nums[1]));
        while(ag.listValHygro.size() > 25)
          ((LinkedList)ag.listValHygro).removeFirst();
      }
      else if(nums[0].equals("temp")){
        ag.listValTemp.add(Integer.parseInt(nums[1]));
        while(ag.listValTemp.size() > 25)
          ((LinkedList)ag.listValTemp).removeFirst();
      } 
    }
  }
  
  if(myPort2.available() > 0){
    val2 = myPort2.readStringUntil('\n');
    println("msg from portail : " + val2);
    nums2 = split(val2,":");
    if(nums2.length > 1){
        nums2[1] = nums2[1].replaceAll("\r", "");
        ag.etat_portail = nums2[1].replaceAll("\n", "");
        ag.sendToServerPortail();
    }
  }
  delay(500); 
  if(++time == 12) {
    ag.sendToServerValue();
    time = 0;
  }
   
}

Integer ouverturePorte(){ //<>//
  myPort2.write(ag.cmdPortail + "\n"); //<>//
  return 0;
}
