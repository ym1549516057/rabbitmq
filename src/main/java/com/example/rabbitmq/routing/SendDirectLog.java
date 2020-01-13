package com.example.rabbitmq.routing;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 15:34
 */
public class SendDirectLog {

    public static final String EXCHANGE_NAME = "direct_logs";

    /**
     * 路由关键字
     */
    public static final String[] routingKeys = new String[]{"info", "warning", "error"};

    public static void send() throws IOException, TimeoutException {
        Connection connection = null;

        Channel channel = RabbitCommon.createChannel(connection);

        //生命交换器为直连
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        for (String routingKey : routingKeys) {
            String message = "send the message level:" + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("send + " + routingKey + ":" + message);
        }

        RabbitCommon.close(connection, channel);
    }

    public static void main(String[] args) {
        try {
            send();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
