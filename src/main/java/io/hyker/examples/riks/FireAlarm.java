package io.hyker.examples.riks;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by joakimb on 3/29/17.
 */
public class FireAlarm implements PubSubClient.SubscriberCallback {

    private final String topic;
    private final PubSubClient client;
    private final Runnable alarmLoop;
    private Thread alarmThread;
    private boolean alarmIsRunning;


    public FireAlarm(final String topic, String broker, String clientID)
            throws MqttException {
        this.topic = topic;
        alarmIsRunning = false;
        client = new PubSubClient(broker, clientID, this);
        alarmLoop = new Runnable() {

            public void run() {
                try {
                    int i = 0;
                    while (!Thread.currentThread().isInterrupted()) {

                        i++;
                        client.publish(topic, "FIRE" + i);
                        Thread.sleep(500);

                    }
                } catch (MqttException e) {
                    System.out.println("Due to unfortunate events, the alarm is sadly not connected...");
                } catch (InterruptedException e) {
                }
            }

        };
    }

    public synchronized void trigger(){
        if (alarmIsRunning){
            return;
        }
        alarmThread = new Thread(alarmLoop);
        alarmThread.start();
        alarmIsRunning = true;
    }

    public synchronized void reset(){
        if (!alarmIsRunning){
            return;
        }
        alarmThread.interrupt();
        alarmIsRunning = false;
        alarmThread = null;
    }

    @Override
    public void messageReceived(String topic, String message) {
        System.out.println("TOPIC: " + topic + " MESSAGE: " + message);
    }
}
