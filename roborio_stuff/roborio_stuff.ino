#include <LiquidCrystal.h>
String inData;
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

void setup() {
  // put your setup code here, to run once:
  pinMode(LED_BUILTIN, OUTPUT);
  Serial.begin(9600);
  Serial.setTimeout(50);
  lcd.begin(16,2);
  digitalWrite(LED_BUILTIN,HIGH);
  delay(500);
  digitalWrite(LED_BUILTIN,LOW);
}

void loop() {
  // put your main code here, to run repeatedly:
  String recieved = Serial.readString();
    if (recieved == "on") {
      digitalWrite(13,HIGH);
    }
    if (recieved == "blink twice") {
      digitalWrite(13,HIGH);
      delay(250);
      digitalWrite(13,LOW);
      delay(250);
      digitalWrite(13,HIGH);
      delay(250);
      digitalWrite(13,LOW);
    }
    if (recieved == "blink once") {
      digitalWrite(13,HIGH);
      delay(250);
      digitalWrite(13,LOW);   
    }
    if (recieved == "off") {
      digitalWrite(13,LOW);
    }
    if (recieved == "strobe") {
      while (recieved != "stop") {
        recieved = Serial.readString();
        digitalWrite(13,HIGH);
        delay(150);
        digitalWrite(13,LOW);
        delay(150);
      }
    }
    lcd.print(recieved);
}
