package io.hyker.examples.riks;

import io.hyker.cryptobox.CryptoBoxCredentialsException;
import io.hyker.riks.box.RiksKit;
import io.hyker.riks.box.RiksWhitelist;
import io.hyker.security.publickeylookup.PublicKeyLookupException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by joakimb on 4/3/17.
 */
public class FleetOwner implements PubSubClient.SubscriberCallback {

    private final PubSubClient client;
    private final RiksKit riksKit;
    private Car car;
    private String uid;

    public FleetOwner(String broker, String clientID, ArrayList<Car> cars)
            throws MqttException, GeneralSecurityException, CryptoBoxCredentialsException, PublicKeyLookupException, IOException {
        car = null;
        uid = '#' + UUID.randomUUID().toString();
        riksKit = new RiksKit(uid, "asdqwe", "config_files/default.config", initRiksWhitelist());
        client = new PubSubClient(broker, clientID, riksKit);

        for (Car car : cars){
            client.subscribe(car.getName());
        }
    }

    public String getCryptoId(){
        return uid;
    }

    private static RiksWhitelist initRiksWhitelist() {
        return new RiksWhitelist() {
            public boolean allowedForKey(String uid, String namespace, String keyId) {
                return true;
            }
            public void newKey(String keyId) {
            }
        };
    }

    public void messageReceived(String topic, String message) {
        System.out.println("TOPIC: " + topic + " MESSAGE: " + message);
    }

}
