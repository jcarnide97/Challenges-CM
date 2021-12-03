package com.minichallenge21;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.util.ArrayList;

public class MQTTHelper {
    public MqttAndroidClient client;
    final String server = "tcp://broker.hivemq.com:1883";
    final String clientId = MqttClient.generateClientId();
    private DBOperations DB;

    public MQTTHelper(Context context) {
        client = new MqttAndroidClient(context, server, clientId);

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        connect();
    }


    public void setCallback(MqttCallbackExtended callback) {
        client.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    client.setBufferOpts(disconnectedBufferOptions);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + server + exception.toString());
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void publishNote(String topicName, String noteTitle, String noteDetails) {
        String message = noteTitle + "###" + noteDetails;
        try {
            client.publish(topicName, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topicName) {
        try {
            client.subscribe(topicName, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("SUBSCRIBED!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("SUBSCRIPTION FAILED!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception subscribing");
            ex.printStackTrace();
        }
    }

    public void unsubscribeToTopic(String topicName) {
        try {
            client.unsubscribe(topicName);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            client.disconnect();
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
}
