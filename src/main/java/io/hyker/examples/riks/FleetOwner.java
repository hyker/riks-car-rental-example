package io.hyker.examples.riks;

import io.apptimate.cryptobox.CryptoBox;
import io.apptimate.cryptobox.CryptoBoxCredentialsException;
import io.apptimate.riks.box.RiksKit;
import io.apptimate.riks.box.RiksWhitelist;
import io.apptimate.security.publickeylookup.PublicKeyLookupException;
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
    private CryptoBox cb;

    public FleetOwner(String broker, String clientID, ArrayList<Car> cars)
            throws MqttException {
        car = null;
        cb = initCryptoBox();
        riksKit = new RiksKit(cb, initRiksWhitelist());
        client = new PubSubClient(broker, clientID, riksKit);

        for (Car car : cars){
            client.subscribe(car.getName());
        }
    }

    public String getCryptoId(){
        return cb.getUID();
    }

    private static CryptoBox initCryptoBox() {
        CryptoBox cryptoBox = null;
        try {
            cryptoBox = new CryptoBox('#' + UUID.randomUUID().toString(), "asdqwe");
        } catch (CryptoBoxCredentialsException e) {
            e.printStackTrace();
        } catch (PublicKeyLookupException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        cryptoBox.start();
        return cryptoBox;
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
