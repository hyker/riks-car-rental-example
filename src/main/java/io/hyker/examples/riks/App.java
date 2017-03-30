package io.hyker.examples.riks;

import io.moquette.server.Server;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.impl.SimpleLogger;

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
        final Server mqttBroker = new Server();
        try {
            mqttBroker.startServer(new File("mqtt.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String topic = "global/alarms";
        String message = "message";

        String broker = "tcp://127.0.0.1:1883";

        try {
            FireAlarm alarm = new FireAlarm(topic, broker, "ALARM");
            MQTTClient subscriber1 = new MQTTClient(broker, "subscriber1");

            subscriber1.subscribe(topic);

            while(true) {
                alarm.trigger();
                Thread.sleep(3000);
                alarm.reset();
                Thread.sleep(3000);
            }

//            publisher.disconnect();
//            subscriber1.disconnect();
//            subscriber2.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
