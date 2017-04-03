package io.hyker.examples.riks;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by joakimb on 4/3/17.
 */
public class Car {

    private PubSubClient client;
    private Thread positionBroadcaster;
    private boolean leased;
    private final Runnable broadcaster;
    private final String carName;

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

    public synchronized void startBroadcasting(PubSubClient pubSubClient){
        if (leased){
            return;
        }
        this.client = pubSubClient;
        positionBroadcaster = new Thread(this.broadcaster);
        positionBroadcaster.start();
        leased = true;
    }

    public synchronized void stopBroadcasting(){
        if (!leased){
            return;
        }
        positionBroadcaster.interrupt();
        leased = false;
        positionBroadcaster = null;
    }

    private String getPosition(){
        if (Math.random() < 0.9){
            return "55.604174, 13.004587"; //Malmoe
        } else {
            return "55.667951, 12.558984"; //Copenhagen, car left the country
        }
    }

    public String getName(){
        return carName;
    }

}
