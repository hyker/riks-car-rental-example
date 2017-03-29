package io.hyker.examples.riks;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by joakimb on 3/29/17.
 */
public class MQTTClient extends MqttClient implements MqttCallback{

    private String clientId;

    public MQTTClient(String broker, String clientId) throws MqttException {
        super(broker,clientId,new MemoryPersistence());

        this.clientId = clientId;
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        this.connect(connOpts);
        setCallback(this);
    }

    public void publish(String topic, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        publish(topic, mqttMessage);
    }

    public void connectionLost(Throwable throwable) {
        System.out.println("(" + clientId + ") CONNECTION LOST");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String messageBody = new String(mqttMessage.getPayload());
        System.out.println("(" + clientId + ") MESSAGE ARRIVED: " + messageBody);
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("(" + clientId + ") MESSAGE PUBLISHED");
    }
}
