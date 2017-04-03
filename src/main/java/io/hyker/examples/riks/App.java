package io.hyker.examples.riks;

import io.moquette.server.Server;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.IOException;

/**
 * Demo of the RIKS protocol running over MQTT
 *
 * Libraries used:
 * Hyker - RIKS
 * Eclipse - MQTT client paho
 * Moquette - MQTT server
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {

            String broker = "tcp://127.0.0.1:1883";

            final Server mqttBroker = new Server();
            mqttBroker.startServer(new File("mqtt.conf"));

            Driver driver = new Driver(broker, "driverA");
            Car car = new Car("car1");
            FleetOwner fleetOwner = new FleetOwner(broker,"fleet_owner");

            //rent a car
            driver.rentCar(car);

            //drive around for a bit
            Thread.sleep(1000);

            //return car
            driver.returnCar();

            //ride a bike
            Thread.sleep(1000);

            //rent car again
            driver.rentCar(car);

            //drive around for a bit
            Thread.sleep(500);

            //car is stolen, give access to fleet owner
            driver.giveAccess(fleetOwner);

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
