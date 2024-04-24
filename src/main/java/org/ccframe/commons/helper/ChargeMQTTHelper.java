package org.ccframe.commons.helper;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.ccframe.config.GlobalEx;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

@Component
@Log
public class ChargeMQTTHelper implements InitializingBean{

	@Value("${app.mqtt.userName}")
	private String userName;
	
	@Value("${app.mqtt.password}")
	private String password;
	
    @Value("${app.mqtt.server}")
	private String server;

    @Value("${app.mqtt.topicPath}")
    private String topicPath;

    private MqttClient client;
    
	@Override
	public void afterPropertiesSet() throws Exception {
		String clientID = UUID.randomUUID().toString();
		
		client = new MqttClient("tcp://" + server, clientID, new MemoryPersistence());
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(password.toCharArray());
		options.setConnectionTimeout(10);
		options.setKeepAliveInterval(20);
		options.setAutomaticReconnect(true); //自动重连
		
		client.setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {
				log.info("断线重连...");
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
			}

		});
		client.connect(options);
		log.info("MQTT Client connect to " + server);
	}

	public void sendMessage(String topic, String json) {
		sendMessage(topic, json, 0);
	}

	public void sendTopicPath(String subPath, String json) {
		sendMessage(topicPath + subPath, json, 0);
	}

	public void sendMessage(String topic, String json, int qos) {
		try {
			MqttMessage mqttMessage = new MqttMessage(json.getBytes("UTF-8"));
			mqttMessage.setQos(qos);
			client.publish(topic, mqttMessage);
		} catch (UnsupportedEncodingException | MqttException e) {
			throw new RuntimeException(e);
		}
	}
}
