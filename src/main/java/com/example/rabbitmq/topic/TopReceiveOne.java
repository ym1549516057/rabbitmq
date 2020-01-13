package com.example.rabbitmq.topic;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 16:33
 */
public class TopReceiveOne {

    public static void receive() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);

        channel.exchangeDeclare(TopicSend.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 路由关键字
        String[] routingKeys = new String[]{"*.orange.*"};
        // 绑定路由关键字
        String queue = channel.queueDeclare().getQueue();
        for (String routingKey : routingKeys) {
            channel.queueBind(queue, TopicSend.EXCHANGE_NAME, routingKey);
            System.out.println("ReceiveLogsTopic1 exchange:" + TopicSend.EXCHANGE_NAME + ", queue:" + queue + ", BindRoutingKey:" + routingKey);
        }
        System.out.println("ReceiveLogsTopic1 [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("ReceiveLogsTopic1 [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queue, true, consumer);
    }

    public static void main(String[] args) {
        try {
            receive();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
