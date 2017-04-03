package io.hyker.examples.riks;

import io.apptimate.cryptobox.CryptoBox;
import io.apptimate.cryptobox.CryptoBoxCredentialsException;
import io.apptimate.riks.box.RiksKit;
import io.apptimate.riks.box.RiksWhitelist;
import io.apptimate.riks.keys.SymKeyExpiredException;
import io.apptimate.riks.message.Message;
import io.apptimate.riks.message.RiksMessage;
import io.apptimate.security.publickeylookup.PublicKeyLookupException;
import org.bouncycastle.crypto.CryptoException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

/**
 * Created by joakimb on 3/29/17.
 */
public class PubSubClient extends MqttClient implements MqttCallback{

    private String clientId;
    private RiksKit riksKit;

    public PubSubClient(String broker, String clientId, RiksKit riksKit) throws MqttException {
        super(broker,clientId,new MemoryPersistence());

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

    public void connectionLost(Throwable throwable) {
        System.out.println("(" + clientId + ") CONNECTION LOST");
    }

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String messageBody = new String(mqttMessage.getPayload());
        System.out.println("encrypted: " + messageBody);
        Message riksMessage = riksKit.decryptMessage(messageBody);
        String decryptedMessage = riksMessage.secret;
        System.out.println("decrypted: " + decryptedMessage);
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("(" + clientId + ") MESSAGE PUBLISHED");
    }

    public interface SubscriberCallback{
        void messageReceived(String topic, String message);
    }



}
