package io.hyker.examples.riks;

import io.hyker.cryptobox.CryptoBox;
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
public class Driver implements RiksWhitelist {

    private final PubSubClient client;
    private final RiksKit riksKit;
    private Car car;
    private ArrayList<String> allowedIds;
    private String uid;

    public Driver(String broker, String clientID)
            throws MqttException, GeneralSecurityException, CryptoBoxCredentialsException, PublicKeyLookupException, IOException {
        car = null;
        uid = '#' + UUID.randomUUID().toString();
        riksKit = new RiksKit(uid, "asdqwe", "config_files/default.config", this);
        client = new PubSubClient(broker, clientID, riksKit);
        allowedIds = new ArrayList<>();

    }

    public synchronized void rentCar(Car car) throws Exception {
        if  (this.car != null){
            throw new Exception("Already renting a car");
        }
        this.car = car;

        //Make sure we have a new encryption key even if we rent the same
        //car more than once.
        riksKit.rekey(car.getName());

        //Make the car broadcast it's encrypted position
        car.startBroadcasting(client);
    }

    public synchronized void returnCar() throws Exception {
        if (car == null){
            throw new Exception("No car rented.");
        }
        //make the car stop broadcasting it's position
        car.stopBroadcasting();
        car = null;
    }

    public void giveAccess(FleetOwner fleetOwner){
        allowedIds.add(fleetOwner.getCryptoId());
    }

    @Override
    public boolean allowedForKey(String uid, String namespace, String keyId) {
        return allowedIds.contains(uid);
    }

    @Override
    public void newKey(String s) {

    }
}
