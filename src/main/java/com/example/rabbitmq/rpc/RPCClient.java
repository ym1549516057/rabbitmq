package com.example.rabbitmq.rpc;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;
import org.springframework.amqp.rabbit.listener.BlockingQueueConsumer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 17:17
 */
public class RPCClient {
    private static String request_queue = "rpc_queue";

    /**
     * 回调队列
     */


    private static String call(String message) throws IOException, InterruptedException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);
        //随机成成一个回调队列
        String replyQueue = channel.queueDeclare().getQueue();
        //随机生成一个ID
        final String corrId = UUID.randomUUID().toString();


        //生命一个消费者来消费回调队列
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    String response = new String(body, StandardCharsets.UTF_8);
                    System.out.println(response);
                }
            }
        };

        //监听回调队列
        channel.basicConsume(replyQueue, true, consumer);

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueue).build();
        channel.basicPublish("", request_queue, basicProperties, message.getBytes(StandardCharsets.UTF_8));
        return null;
    }

    public static void main(String[] args) {
        try {
//            System.out.println("RPCClient [x] Requesting fib(30)");
            for (int i = 1; i < 5; i++) {
                call(i + "0");
            }
//            System.out.println("RPCClient [.] Got '" + response + "'");
        } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
