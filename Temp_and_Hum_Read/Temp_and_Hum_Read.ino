// Libreria DHT
#include "DHT.h"

// Pin digitale di arduino connesso al DHT
#define DHTPIN 3

#define LDR_PIN A0

// tipo del sensore: DHT 11
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(9600);
  Serial.println(F("DHTxx test!"));

  dht.begin();
}

void loop() {
  // Attesa di 2 secondi prima di fornire la misura.
  delay(2000);

  // Lettura dell'umidità
  float h = dht.readHumidity();
  // Lettura della temperatura in gradi Celsius
  float t = dht.readTemperature();

  // Verifica se le si presenta un errore di lettura (e riprova nuovamente)
  if (isnan(h) || isnan(t)) {
    Serial.println(F("Impossibile leggere dal sensore DHT!"));
    return;
  }


  float luminosita = misuraLuminosita();

  Serial.print(h);
  Serial.print(",");
  Serial.print(t);
  Serial.print(",");
  Serial.println(luminosita);
}
float misuraLuminosita() {
  int valore_ldr = analogRead(LDR_PIN);
  float val_ldr_convertito = map(valore_ldr, 20, 1005, 0, 100);

  return val_ldr_convertito;
}