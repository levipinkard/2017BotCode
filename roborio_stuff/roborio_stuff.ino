String inData;

void setup() {
  // put your setup code here, to run once:
pinMode(13, OUTPUT);
Serial.begin(9600);
Serial.setTimeout(190);
}

void loop() {
  // put your main code here, to run repeatedly:
        byte recieved = Serial.read();
            if (recieved == 1) {
              digitalWrite(13,HIGH);
            }
            if (recieved == 2) {
              digitalWrite(13,LOW);
            }

}
