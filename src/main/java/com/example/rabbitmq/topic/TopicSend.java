package com.example.rabbitmq.topic;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 16:25
 */
public class TopicSend {

    public static final String EXCHANGE_NAME = "topic_logs";

    /**
     * 戴发送消息的路径
     */
    private static final String[] routingKeys = new String[]{"quick.orange.rabbit",
            "lazy.orange.elephant",
            "quick.orange.fox",
            "lazy.brown.fox",
            "quick.brown.fox",
            "quick.orange.male.rabbit",
            "lazy.orange.male.rabbit"};

    public static void sendLog() throws IOException, TimeoutException {
        Connection connection = null;

        Channel channel = RabbitCommon.createChannel(connection);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        for (String routingKey : routingKeys) {
            String message = "FROM " + routingKey + " message";
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("TopicSend [x] Sent '" + routingKey + "':'" + message + "'");
        }
        RabbitCommon.close(connection,channel);
    }

    public static void main(String[] args) {
        try {
            sendLog();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
