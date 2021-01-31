
static FILE uartout = {0};
static int uart_putchar (char c, FILE *stream)
{
  Serial.write(c) ;
  return 0 ;
}

int hygro = A0, temp = A1;    // select the input pin for the potentiometers
int ledPin = 13;      // select the pin for the LED
int hygroValue = 0, tempValue = 0;  // variable to store the value coming from the sensor

void setup() {
  // declare the ledPin as an OUTPUT:
  pinMode(ledPin, OUTPUT);

  Serial.begin(9600);
  fdev_setup_stream (&uartout, uart_putchar, NULL, _FDEV_SETUP_WRITE);
  stdout = &uartout ;
}

void loop() {
  // read the value from the sensor:
  hygroValue = analogRead(hygro);
  tempValue = analogRead(temp);
  printf("hygro:%d\n", hygroValue);
  printf("temp:%d\n", tempValue);
  // turn the ledPin on
  digitalWrite(ledPin, !digitalRead(ledPin));
  delay(1000);
}
