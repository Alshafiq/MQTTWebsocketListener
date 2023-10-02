package com.example.mqttwebsocketlistener;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private TextView textMessage;
    private Button buttonConnect, buttonDisconnect;

    private IMqttAsyncClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMessage = findViewById(R.id.textView);
        buttonConnect = findViewById(R.id.button);
        buttonDisconnect = findViewById(R.id.button2);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
    }

    private void connect() {
        String clientId = MqttClient.generateClientId();
        String uriString = "ws://18.139.189.221:9001";
        //String userName = "your-user-name";
        //String password = "your-password";

        try {
            client = new Jdk16MqttWebSocketAsyncClient(uriString, clientId, new MemoryPersistence());
            final MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            options.setCleanSession(true);
            //options.setUserName(userName);
            //options.setPassword(password.toCharArray());
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setSubscription();
                    textMessage.setText("Connected!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // on connection failure
                    textMessage.setText("Fail to connected!");
                }
            });

            client.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    textMessage.setText("Message: " + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    textMessage.setText("Delivery: " + token);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    textMessage.setText("Connection lost: " + cause.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try{
            client.disconnect();
            textMessage.setText("Disconnected!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSubscription(){
        try{
            client.subscribe("tempserver/ui/mqtt-in",1);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}