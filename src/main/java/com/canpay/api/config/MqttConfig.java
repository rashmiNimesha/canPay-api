package com.canpay.api.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Bean
    public MqttClient mqttClient() throws MqttException {
//        String brokerUrl = "tcp://localhost:1883";
//        String brokerUrl = "mqtt-v1-canpay.sehanw.com";
        String brokerUrl = "tcp://mqtt-v1-canpay.sehanw.com:1883";
        String clientId = "backend_" + System.currentTimeMillis();
        MqttClient client = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        client.connect(options);
        logger.info("Connected to MQTT broker: {}", brokerUrl);
        return client;
    }
}
