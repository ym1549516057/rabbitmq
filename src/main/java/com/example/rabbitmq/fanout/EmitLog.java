package com.example.rabbitmq.fanout;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 14:51
 */
public class EmitLog {
    private static final String EXCHAGE_NAME = "logs";

    private static void publish() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);
        //创建交换器
        channel.exchangeDeclare(EXCHAGE_NAME, BuiltinExchangeType.FANOUT);

        for (int i = 0; i < 5; i++) {
            String message = "hello world " + i;
            channel.basicPublish(EXCHAGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(message);
        }

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
