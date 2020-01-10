package com.example.rabbitmq.helloworld;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/10 11:36
 */
public class Product {
    private static final String QUEUE_NAME = "hello";

    private static void publish() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);
        //声明一个队列
        //-- 在RabbitMQ中，队列声明是幂等性的（一个幂等操作的特点是其任意多次执行所产生的影响均与一次执行的影响相同），
        // 也就是说，如果不存在，就创建，如果存在，不会对已经存在的队列产生任何影响。
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "hello world three";

        //发送到消息队列中
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("发布成功");
        RabbitCommon.close(connection, channel);
    }

    public static void main(String[] args) {
        try {
            publish();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
