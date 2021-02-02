package serial_port;

import processing.serial.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Callable;

Serial myPort1;
Serial myPort2;
String val;
String val2;
String[] nums;
String[] nums2;
Agent ag;

void setup()
{
  ag = new Agent();
  // COM7 sont les entrées et COM8 sont les sorties
  myPort1 = new Serial(this, "COM3", 9600);
  myPort2 = new Serial(this, "COM6", 9600);
  this.ag.start(new Callable<Void>() {
    public Integer call() {
        return ouverturePorte();
    }
  });
  
}
 //<>//
void draw()
{
  if ( myPort1.available() > 0) {  // If data is available,
    val = myPort1.readStringUntil('\n');         // read it and store it in val
    nums = split(val,':');
    if(nums.length > 1){
      if(nums[0].equals("hygro")){
        ag.listValHygro.add(Integer.parseInt(nums[1]));
        while(ag.listValHygro.size() > 25)
          ag.listValHygro.removeFirst();
      }
      else if(nums[0].equals("temp")){
        ag.listValTemp.add(Integer.parseInt(nums[1]));
        while(ag.listValTemp.size() > 25)
          ag.listValTemp.removeFirst();
      } 
    }
  }
  
  if(myPort2.available() > 0){
    val2 = myPort2.readStringUntil('\n');
    nums2 = split(val2,":");
    if(nums2.length > 1){
        ag.etat_portail = nums2[1];
    }
  }
  
  delay(500);  
}

void ouverturePorte(){
  myPort2.write(ag.cmdPortail);
}
