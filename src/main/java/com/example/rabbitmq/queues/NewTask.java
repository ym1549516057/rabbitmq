package com.example.rabbitmq.queues;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class NewTask {

    public static final String TASK_QUEUE = "task_queue";

    public static void publish() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);
        channel.queueDeclare(TASK_QUEUE, true, false, false, null);
        //分发消息
        for (int i = 0; i <= 5; i++) {
            String message = "hello world" + i;
            channel.basicPublish("", TASK_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("publish:" + message);
        }
        RabbitCommon.close(connection, channel);
    }

    public static void main(String[] args) {
        try {
            publish();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
