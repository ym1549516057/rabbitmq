package com.example.rabbitmq.helloworld;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/10 17:56
 */
public class Customer {

    public static final String QUEUE_NAME = "hello";

    public static void testCustomer() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);
        //生命要关注的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //		DefaultConsumer类实现了Consumer接口，通过传入一个频道，
        //		告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("C [x] Received '" + message + "'");
            }
        };
        //自动回复队列应答
        channel.basicConsume(QUEUE_NAME, consumer);
    }

    public static void main(String[] args) {

        try {
            testCustomer();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
