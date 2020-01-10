package com.example.rabbitmq.queues;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class CustomerOne {


    public static void recived() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);
        channel.queueDeclare(NewTask.TASK_QUEUE, true, false, false, null);
        System.out.println("customer1 wait for message.to exit press ctrl c");
        //每次从队列中获取数量
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("customer1 received " + message);

                try {
                    doWork();
                } finally {
                    System.out.println("customer1 done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        //消息消费确认完成
        channel.basicConsume(NewTask.TASK_QUEUE, false, consumer);
    }

    public static void doWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        try {
            recived();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
