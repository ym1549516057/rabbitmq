package com.example.rabbitmq.rpc;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 20:32
 */
public class Client {
    public static void excute(String message) {

        try {
//            Thread.sleep(5000);
            Connection connection = null;
            Channel channel = RabbitCommon.createChannel(connection);

            // 预先定义响应的结果，即预先订阅响应结果的队列，先订阅响应队列，再发送消息到请求队列
            String reyply_to_queue = channel.queueDeclare().getQueue();
            final String correlationId = UUID.randomUUID().toString();
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(correlationId)) {
                        String message = new String(body, StandardCharsets.UTF_8);
                        System.out.println("已接收到服务器的响应结果：" + message);
                    }
                }
            };
            channel.basicConsume(reyply_to_queue, true, consumer);


            // 将消息发送到请求队列中
            String rpc_queuqu = "rpc_queue";
//            String message = "Hello RabbitMQ";
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlationId).replyTo(reyply_to_queue).build();
            channel.basicPublish("", rpc_queuqu, properties, message.getBytes("UTF-8"));
            System.out.println("已发出请求请求消息：" + message);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String s = "hello";
        for (int i = 0; i < 5; i++) {
            excute(s + i);
        }
    }
}
