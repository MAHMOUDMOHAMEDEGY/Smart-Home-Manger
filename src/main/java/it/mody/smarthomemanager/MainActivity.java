package it.mody.smarthomemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.green;

public class MainActivity extends AppCompatActivity {
    static final String MQTTHOST_FAN = "tcp://group-project.cloud.shiftr.io:1883";
    static final String MQTTUSER_FAN = "group-project";
    static final String MQTTPASS_FAN = "rsJY4VijrFp5U21X";
    static final String TOPIC_FAN = "Fan";
    static final String TOPIC_MSG_ON_FAN = "ON";
    static final String TOPIC_MSG_OFF_FAN = "OFF";

    static final String MQTTHOST_LIGHT = "tcp://group-project.cloud.shiftr.io:1883";
    static final String MQTTUSER_LIGHT = "group-project";
    static final String MQTTPASS_LIGHT = "zHeuGXJcjmZLtcQR";
    static final String TOPIC_LIGHT = "light";
    static final String TOPIC_MSG_ON_LIGHT = "ON";
    static final String TOPIC_MSG_OFF_LIGHT = "OFF";

    Boolean PermessoPubblicare;
    MqttAndroidClient cliente;
    MqttConnectOptions Opzioni;
    String clienteID = "";

    TextView tXTIdCliente;
    TextView textInfoFan;
    TextView textInfoLight;
    private boolean fanConnected = false;
    private boolean lightConnected = false;
    private boolean connectedMessageShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tXTIdCliente = findViewById(R.id.tXTIdCliente);
        textInfoFan = findViewById(R.id.textINFO_FAN);
        textInfoLight = findViewById(R.id.textINFO_LIGHT);

        ottenereNomeCliente();
        connectToBroker(MQTTHOST_FAN, MQTTUSER_FAN, MQTTPASS_FAN, TOPIC_FAN, textInfoFan);
        connectToBroker(MQTTHOST_LIGHT, MQTTUSER_LIGHT, MQTTPASS_LIGHT, TOPIC_LIGHT, textInfoLight);

        Button btnext_FAN = findViewById(R.id.control_panel);
        btnext_FAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_FAN = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i_FAN);
            }
        });

        setupButtonActions();
    }

    private void setupButtonActions() {
        setupFanButtons();
        setupLightButtons();
    }

    private void setupFanButtons() {
        Button btON_FAN = findViewById(R.id.btON_FAN);
        btON_FAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviareMessaggio(TOPIC_FAN, TOPIC_MSG_ON_FAN);
            }
        });

        Button btOFF_FAN = findViewById(R.id.btOFF_FAN);
        btOFF_FAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviareMessaggio(TOPIC_FAN, TOPIC_MSG_OFF_FAN);
            }
        });
    }

    private void setupLightButtons() {
        Button btON_LIGHT = findViewById(R.id.btON_LIGHT);
        btON_LIGHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviareMessaggio(TOPIC_LIGHT, TOPIC_MSG_ON_LIGHT);
            }
        });

        Button btOFF_LIGHT = findViewById(R.id.btOFF_LIGHT);
        btOFF_LIGHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviareMessaggio(TOPIC_LIGHT, TOPIC_MSG_OFF_LIGHT);
            }
        });
    }

    private void SubscribeToTopic(String topic, final TextView txtInfo) {
        try {
            cliente.subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        cliente.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getBaseContext(), "Connection LOST", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals(TOPIC_FAN)) {
                    String msg = new String(message.getPayload());
                    if (msg.equals(TOPIC_MSG_ON_FAN)) {
                        textInfoFan.setText("Fan ON");
                        textInfoFan.setBackgroundColor(GREEN);
                    } else if (msg.equals(TOPIC_MSG_OFF_FAN)) {
                        textInfoFan.setText("Fan OFF");
                        textInfoFan.setBackgroundColor(RED);
                    }
                } else if (topic.equals(TOPIC_LIGHT)) {
                    String msg = new String(message.getPayload());
                    if (msg.equals(TOPIC_MSG_ON_LIGHT)) {
                        textInfoLight.setText("Light ON");
                        textInfoLight.setBackgroundColor(GREEN);
                    } else if (msg.equals(TOPIC_MSG_OFF_LIGHT)) {
                        textInfoLight.setText("Light OFF");
                        textInfoLight.setBackgroundColor(RED);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void InviareMessaggio(String topic, String msg) {
        checkConnection();
        if (PermessoPubblicare) {
            try {
                int qos = 0;
                cliente.publish(topic, msg.getBytes(), qos, false);
                Toast.makeText(getBaseContext(), topic + " : " + msg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkConnection() {
        if (cliente.isConnected()) {
            PermessoPubblicare = true;
        } else {
            PermessoPubblicare = false;
            connectToBroker(MQTTHOST_FAN, MQTTUSER_FAN, MQTTPASS_FAN, TOPIC_FAN, textInfoFan);
        }
    }

    private void ottenereNomeCliente() {
        String manufacturer = Build.MANUFACTURER;
        String modelName = Build.MODEL;
        clienteID = manufacturer + " " + modelName;
        tXTIdCliente.setText(clienteID);
    }

    private void connectToBroker(String host, String user, String password, String topic, TextView txtInfo) {
        cliente = new MqttAndroidClient(this.getApplicationContext(), host, clienteID);
        Opzioni = new MqttConnectOptions();
        Opzioni.setUserName(user);
        Opzioni.setPassword(password.toCharArray());

        try {
            IMqttToken token = cliente.connect(Opzioni);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Controlla quale topic è stato connesso e imposta il relativo flag di connessione
                    if (topic.equals(TOPIC_FAN)) {
                        fanConnected = true;
                    } else if (topic.equals(TOPIC_LIGHT)) {
                        lightConnected = true;
                    }

                    // Se entrambi i dispositivi sono connessi e il messaggio non è ancora stato mostrato, mostra "CONNECTED"
                    if (fanConnected && lightConnected && !connectedMessageShown) {
                        Toast.makeText(getBaseContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
                        connectedMessageShown = true; // Imposta il flag a true per indicare che il messaggio è stato mostrato
                    }

                    // Sottoscrivi al topic solo se la connessione è stata stabilita con successo
                    if (fanConnected || lightConnected) {
                        SubscribeToTopic(topic, txtInfo);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getBaseContext(), "CONNECTION FAILED", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnetti il client MQTT e fermalo
        disconnectClient();
    }

    private void disconnectClient() {
        if (cliente != null && cliente.isConnected()) {
            try {
                cliente.disconnect();
                cliente.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
