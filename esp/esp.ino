#include <ESP8266WiFi.h>
#include <MQTT.h>

const char ssid[] = "Mody";
const char pass[] = "34258299";

WiFiClient net;
MQTTClient client;
const int ledPin = D1; // LED pin on the ESP8266//D1
const int fanPin = D2; // fan pin on the ESP8266//D2

void connect_light() {
  Serial.print("Checking WiFi....");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(1000);
  }

  Serial.print("\nConnecting to the MQTT server....");
  while (!client.connect("arduino_light", "group-project", "zHeuGXJcjmZLtcQR")) {
    Serial.print(".");
    delay(1000);
  }

  Serial.println("\nConnected to the MQTT server_light!");
  
  client.subscribe("light");
}

void messageReceived_light(String &topic, String &payload) {
  Serial.println("Message received on the topic: " + topic + " - " + payload);

  if (topic == "light") {
    if (payload == "ON") {
      digitalWrite(ledPin, HIGH); // Turn on the LED
    } else if (payload == "OFF") {
      digitalWrite(ledPin, LOW); // Turn off the LED
    }
  }
}
void connect_fan() {


  Serial.print("\nConnecting to the MQTT server....");
  while (!client.connect("arduino_fan", "group-project", "rsJY4VijrFp5U21X")) {
    Serial.print(".");
    delay(1000);
  }

  Serial.println("\nConnected to the MQTT server_Fan!");
  
  client.subscribe("Fan");
}

void messageReceived_fan(String &topic, String &payload) {
  Serial.println("Message received on the topic: " + topic + " - " + payload);

  if (topic == "Fan") {
    if (payload == "ON") {
      digitalWrite(fanPin, HIGH); // Turn on the FAN
    } else if (payload == "OFF") {
      digitalWrite(fanPin, LOW); //Turn off the FAN
    }
  }
}
void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, pass);
  pinMode(ledPin, OUTPUT); //Set up the LED pin as an output
  pinMode(fanPin, OUTPUT); //Set up the fan pin as an output
  client.begin("group-project.cloud.shiftr.io", net);
  client.onMessage(messageReceived_light);
  client.onMessage(messageReceived_fan);
  connect_light();
  connect_fan();
}

void loop() {
  client.loop();
  delay(10); 

  if (!client.connected()) {
    connect_light();
    connect_fan();
  }
}