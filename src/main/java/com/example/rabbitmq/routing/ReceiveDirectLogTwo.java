package com.example.rabbitmq.routing;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 15:44
 */
public class ReceiveDirectLogTwo {

    private static final String[] routingKeys = new String[]{"error"};

    public static void receive() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);

        channel.exchangeDeclare(SendDirectLog.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //获取匿名队列
        String queue = channel.queueDeclare().getQueue();
        //根据路由关键字进行多重绑定
        for (String routingKey : routingKeys) {
            channel.queueBind(queue, SendDirectLog.EXCHANGE_NAME, routingKey);
            System.out.println("ReceiveLogDirectTwo exchange:" + SendDirectLog.EXCHANGE_NAME + ",queueName:" + queue + ",routingKey:" + routingKey);
        }
        System.out.println("ReceiveLogsDirectTwo [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
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
