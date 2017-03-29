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
            MQTTClient publisher = new MQTTClient(broker, "publisher");
            MQTTClient subscriber1 = new MQTTClient(broker, "subscriber1");
            MQTTClient subscriber2 = new MQTTClient(broker, "subscriber2");

            subscriber1.subscribe(topic);
            subscriber2.subscribe(topic);

            for (int i = 0; i < 5; i++) {
                publisher.publish(topic,message + " " + i);
            }


//            publisher.disconnect();
//            subscriber1.disconnect();
//            subscriber2.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
}
