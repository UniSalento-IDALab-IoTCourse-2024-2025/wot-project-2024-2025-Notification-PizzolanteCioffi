package it.unisalento.iot2425.watchapp.notification.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class MqttSubscriberConfig {

    private static final String BROKER_URL = "tcp://23.21.148.21:1883";
    private static final String CLIENT_ID = "spring-mqtt-subscriber";
    public static final String TOPICREGISTRATION = "registration";
    public static final String TOPICDATA = "data";

    private MqttClient mqttClient;

    @Autowired
    MqttMessageListener mqttMessageListener;

    @PostConstruct
    public void init() {
        try {

            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);

            mqttClient.connect(options);

            ObjectMapper objectMapper = new ObjectMapper();



            mqttClient.subscribe(TOPICREGISTRATION, (topic, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Ricevuto messaggio su " + topic + ": " + payload);
                Map<String, Object> map = objectMapper.readValue(payload, Map.class);
                mqttMessageListener.handleMessage(topic, map);
            });

            mqttClient.subscribe(TOPICDATA, (topic, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Ricevuto messaggio su " + topic + ": " + payload);
                Map<String, Object> map = objectMapper.readValue(payload, Map.class);
                mqttMessageListener.handleMessage(topic, map);
            });

            System.out.println("MQTT Subscriber connesso e in ascolto su: " + TOPICREGISTRATION + " e " + TOPICDATA);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
