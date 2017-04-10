package io.hyker.examples.riks;

import io.moquette.server.Server;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Demo of the RIKS protocol running over MQTT
 * Scenario is described at hyker.io
 *
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

            //broker running on localhost
            String broker = "tcp://127.0.0.1:1883";

            //start up the MQTT broker
            final Server mqttBroker = new Server();
            mqttBroker.startServer(new File("mqtt.conf"));

            //create a driver who can rent cars
            Driver driver = new Driver(broker, "driverA");

            //create a car
            Car car = new Car("car1");

            //add the car to rental service belonging to the fleet owner
            ArrayList<Car> fleet = new ArrayList<>();
            fleet.add(car);
            FleetOwner fleetOwner = new FleetOwner(broker,"fleet_owner" ,fleet);

            /*
            At this point, the car is not broadcasting its position as
            there is currently no one renting it. As soon as the next line is executed,
            i.e. somebody rents the car, the car will start broadcasting its position.
            The position data will be encrypted with a key known only to the renter
            of the car, so there is no privacy infringement.

            As soon as the car is returned, it will stop broadcasting its position.
             */

            //rent a car (car will start broadcasting its poisition in ecrypted form)
            driver.rentCar(car);

            //drive around for a bit
            Thread.sleep(1000);

            //return car (end broadcasting)
            driver.returnCar();

            //rent car again (resume broadcasting, but with a new encryption key)
            driver.rentCar(car);

            //drive around for a bit
            Thread.sleep(500);

            //car is stolen, give access to fleet owner
            driver.giveAccess(fleetOwner);

            /*
             * Now, access to positions from the latest driving session is given to the
             * fleet owner. The owner can track the stolen car but is not given access
             * to previous positions.
             */

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
