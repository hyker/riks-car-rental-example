package io.hyker.examples.riks;

import io.apptimate.cryptobox.CryptoBox;
import io.apptimate.cryptobox.CryptoBoxCredentialsException;
import io.apptimate.riks.box.RiksKit;
import io.apptimate.riks.box.RiksWhitelist;
import io.apptimate.security.publickeylookup.PublicKeyLookupException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

/**
 * Created by joakimb on 4/3/17.
 */
public class Driver {

    private final PubSubClient client;
    private final RiksKit riksKit;
    private Car car;

    public Driver(String broker, String clientID)
            throws MqttException {
        car = null;
        riksKit = new RiksKit(initCryptoBox(), initRiksWhitelist());
        client = new PubSubClient(broker, clientID, riksKit);
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
        //fiddle with whitelist
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

}
