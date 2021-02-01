#define CLOSED 1
#define OPENING 2
#define OPEN 3
#define CLOSING 4

int etatCourant = CLOSED, etatSuivant = CLOSED;

// constants won't change. They're used here to set pin numbers:
const int finCourse = 2;     // the number of the pushbutton pin
const int motor =  13;      // the number of the LED pin

// variables will change:
int finCourseState = 0, motorState = 0;         // variable for reading the pushbutton status

const byte numChars = 32;
char receivedChars[numChars]; // an array to store the received data
boolean newData = false;
int cmd;

void setup() {
  Serial.begin(9600);
  // initialize the LED pin as an output:
  pinMode(motor, OUTPUT);
  // initialize the finCourse pin as an input:
  pinMode(finCourse, INPUT);

  
  Serial.print("portail:");
  Serial.println(etatSuivant);
}

void loop() {
  finCourseState = digitalRead(finCourse);
  recvWithEndMarker();
  
  switch(etatCourant) {
    case CLOSED :
      if(newData) {
        if(cmd == 1) etatSuivant = OPENING;
        else etatSuivant = etatCourant;
        newData = false;
      } else etatSuivant = etatCourant;
      break;

    case OPENING :
      if(finCourseState) etatSuivant = OPEN;
      else etatSuivant = etatCourant;
      break;

    case OPEN :
      if(newData) {
        if(cmd == 0) etatSuivant = CLOSING;
        else etatSuivant = etatCourant;
        newData = false;
      } else etatSuivant = etatCourant;
      break;

    case CLOSING :
      if(finCourseState) etatSuivant = CLOSED;
      else etatSuivant = etatCourant;
      break;
  }

  digitalWrite(motor, etatCourant == OPENING || etatCourant == CLOSING);

  if(etatCourant != etatSuivant) {
    Serial.print("portail:");
    Serial.println(etatSuivant);
  }
  etatCourant = etatSuivant;
}

void recvWithEndMarker() {
  static byte ndx = 0;
  char endMarker = '\n';
  char rc;
  
  while (Serial.available() > 0 && newData == false) {
    rc = Serial.read();
    
    if (rc != endMarker) {
      receivedChars[ndx] = rc;
      ndx++;
      if (ndx >= numChars) {
        ndx = numChars - 1;
      }
    } else {
      receivedChars[ndx] = '\0'; // terminate the string
      ndx = 0;

      char* separator = strchr(receivedChars, ':');
      if (separator != 0)
      {
          *separator = 0;
          cmd = atoi(++separator);
      }
    
      newData = true;
    }
  }
}
