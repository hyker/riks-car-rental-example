package io.hyker.examples.riks;

import io.hyker.riks.box.RiksKit;
import io.hyker.riks.keys.SymKeyExpiredException;
import io.hyker.riks.message.Message;
import org.bouncycastle.crypto.CryptoException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.Serializable;

/**
 * Created by joakimb on 3/29/17.
 */
public class PubSubClient extends MqttClient implements MqttCallback {

    private String clientId;
    private RiksKit riksKit;

    public PubSubClient(String broker, String clientId, RiksKit riksKit) throws MqttException {
        super(broker, clientId, new MemoryPersistence());

        this.clientId = clientId;
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        this.connect(connOpts);
        setCallback(this);

        this.riksKit = riksKit;

    }

    public void publish(String topic, String message) throws MqttException {
        try {

            Message m = new Message().secret(message);
            String encrypted = riksKit.encryptMessage(m, topic);


            MqttMessage mqttMessage = new MqttMessage(encrypted.getBytes());
            publish(topic, mqttMessage);

        } catch (SymKeyExpiredException | CryptoException e) {
            throw new MqttException(e);
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("(" + clientId + ") CONNECTION LOST");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String messageBody = new String(mqttMessage.getPayload());
        System.out.println("message arrived on: " + topic + " MESSAGE: " + messageBody);

        riksKit.decryptMessageAsync(messageBody, (message, e) -> {
            System.out.println("going to decrypt");
            String decryptedMessage = message.secret;
            System.out.println("decrypted: " + decryptedMessage);
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("(" + clientId + ") MESSAGE PUBLISHED");
    }

    public interface SubscriberCallback {
        void messageReceived(String topic, String message);
    }
}
