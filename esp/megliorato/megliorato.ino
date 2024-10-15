#include <ESP8266WiFi.h>
#include <MQTT.h>

const char ssid[] = "Mody";
const char pass[] = "34258299";

WiFiClient net;
MQTTClient client;
const int ledPin = D1; // LED pin on the ESP8266
const int fanPin = D2; // Fan pin on the ESP8266

void connectToWiFi() {
  Serial.print("Connecting to WiFi...");
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("Connected to WiFi!");
}

void connectToMQTT() {
  Serial.print("Connecting to MQTT...");
  while (!client.connect("arduino", "group-project", "zHeuGXJcjmZLtcQR")) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("Connected to MQTT!");

  client.subscribe("light");
  client.subscribe("Fan");
}

void messageReceived(String &topic, String &payload) {
  Serial.println("Message received on topic: " + topic + " - " + payload);

  if (topic == "light") {
    if (payload == "ON") {
      digitalWrite(ledPin, HIGH); // Turn on the LED
    } else if (payload == "OFF") {
      digitalWrite(ledPin, LOW); // Turn off the LED
    }
  } else if (topic == "Fan") {
    if (payload == "ON") {
      digitalWrite(fanPin, HIGH); // Turn on the Fan
    } else if (payload == "OFF") {
      digitalWrite(fanPin, LOW); // Turn off the Fan
    }
  }
}

void setup() {
  Serial.begin(115200);
  pinMode(ledPin, OUTPUT); // Set up the LED pin as an output
  pinMode(fanPin, OUTPUT); // Set up the fan pin as an output
  
  connectToWiFi();
  
  client.begin("group-project.cloud.shiftr.io", net);
  client.onMessage(messageReceived);
  
  connectToMQTT();
}

void loop() {
  client.loop();
  delay(10);

  if (!client.connected()) {
    connectToMQTT();
  }
}
