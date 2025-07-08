package com.canpay.api.service.implementation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttService {
    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);
    private final MqttClient mqttClient;

    public MqttService(@Value("${mqtt.broker.url:tcp://localhost:1883}") String brokerUrl,
                       @Value("${mqtt.client.id:canpay-backend-client-${random.uuid}}") String clientId) throws MqttException {
        this.mqttClient = new MqttClient(brokerUrl, clientId, null);
    }

    @PostConstruct
    public void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);
            mqttClient.connect(options);
            logger.info("Connected to MQTT broker at {}", mqttClient.getServerURI());
        } catch (MqttException e) {
            logger.error("Failed to connect to MQTT broker", e);
            throw new RuntimeException("MQTT connection failed", e);
        }
    }

    @PreDestroy
    public void disconnect() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("Disconnected from MQTT broker");
            }
        } catch (MqttException e) {
            logger.error("Failed to disconnect from MQTT broker", e);
        }
    }

    public void sendPaymentNotification(String userId, String role, String message) {
        String topic = String.format("canpay/%s/%s", role.toLowerCase(), userId);
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);
            logger.debug("Sent MQTT message to topic {}: {}", topic, message);
        } catch (MqttException e) {
            logger.error("Failed to send MQTT message to topic {}: {}", topic, message, e);
        }
    }
}