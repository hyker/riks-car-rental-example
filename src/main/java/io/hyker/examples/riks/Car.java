package io.hyker.examples.riks;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * This class represents a car capable of determining its GPS position and broadcasting it.
 * The broadcasting client is an externally provided resource.
 */
public class Car {

    private PubSubClient client;
    private Thread positionBroadcaster;
    private boolean leased;
    private final Runnable broadcaster;
    private final String carName;

    /**
     * Create a new Car.
     * @param carName the identifier of the car
     */
    public Car(String carName){
        this.carName = carName;
        broadcaster = new Runnable() {

            public void run() {
                try {
                    int i = 0;
                    while (!Thread.currentThread().isInterrupted()) {

                        i++;
                        client.publish(carName, getPosition());
                        Thread.sleep(50);

                    }
                } catch (MqttException e) {
                    System.out.println("Mqtt exception.");
                } catch (InterruptedException e) {
                }
            }

        };
    }

    /**
     * Start broadcasting the cars position using the provided broadcaster.
     * @param pubSubClient the broadcaster to use.
     */
    public synchronized void startBroadcasting(PubSubClient pubSubClient){
        if (leased){
            return;
        }
        this.client = pubSubClient;
        positionBroadcaster = new Thread(this.broadcaster);
        positionBroadcaster.start();
        leased = true;
    }

    /**
     * Stop broadcasting the position.
     */
    public synchronized void stopBroadcasting(){
        if (!leased){
            return;
        }
        positionBroadcaster.interrupt();
        leased = false;
        positionBroadcaster = null;
    }

    /**
     * Determine the current position of the car.
     * @return
     */
    private String getPosition(){
        if (Math.random() < 0.9){
            return "55.604174, 13.004587"; //Malmoe
        } else {
            return "55.667951, 12.558984"; //Copenhagen, car left the country
        }
    }

    /**
     * Get the id of the car.
     * @return the id of the car
     */
    public String getName(){
        return carName;
    }

}
