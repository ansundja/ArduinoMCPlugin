#include <Arduino.h>

uint8_t matrix[64];

String comand;
int pin, val;
int scan[256], oldVals[256];

void setup() {
    Serial.begin(9600);
    while (!Serial) {
        ; // wait for serial port to connect. Needed for Leonardo only
    }
    Serial.println("ss boot");
}

// the loop routine runs over and over again forever:
void loop() {

  comand = String("");
  char c = '\0';
  do {
      if (Serial.available() > 0) {
          c = (char)Serial.read();
          comand += c;
      } else {
        listener();
      }
  } while (c != '\n');

  if(comand != "") {
    char charComand = comand.charAt(0);
    int pin = (int)comand.charAt(1)-'@';
    int val = (int)comand.charAt(2)-'@';

    switch(charComand) {
        case 'o': { //set output value
        	if(val > 15) {
        	Serial.println("error value is > 15");
        	break;
        	}
            pinMode(pin, OUTPUT);
            scan[pin] = 0;
            analogWrite(pin, val*17);
            Serial.print("ss output set ");
            Serial.print(pin);
            Serial.print(" ");
            Serial.println(val);
            break;
        }
        case 'd': { //register digital listener
            pinMode(pin, INPUT);
            scan[pin] = 1;
            oldVals[pin] = 0;
            Serial.print("ss digital listener set ");
            Serial.println(pin);
            break;
        }
        case 'a': { //register analog listener
            //digitalWrite(pin, LOW);
            pinMode(pin, INPUT);
            scan[pin] = 2;
            oldVals[pin] = 0;
            Serial.print("ss analog listener set ");
            Serial.println(pin);
            break;
        }
        default: {
            Serial.print("error: comand not found: ");
            Serial.println(comand.charAt(0));

        }
    }
  }
  listener();
}

void listener() {
  //le listener ;)
  for(pin = 0; pin < 256; pin++) {
    switch(scan[pin]){
        case 0: { //pin mode is Output or Pin does not exist
            //nothing to do
            break;
        }
        case 1: {//Digital In
            val = digitalRead(pin);
            if(val != oldVals[pin]) {
                Serial.print("d");
                Serial.print((char)(pin+'@'));
                Serial.println((char)(val+'@'));
                oldVals[pin] = val;
            }
            break;
        }
        case 2: {//Analog in
            val = analogRead(pin)/64;
            if(val != oldVals[pin]) {
                Serial.print("a");
                Serial.print((char)(pin+'@'));
                Serial.println((char)(val+'@'));
                oldVals[pin] = val;
            }
            break;
        }
    }
  }
}
