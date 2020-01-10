package com.example.rabbitmq.common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/10 18:12
 */
public class RabbitCommon {

    public static Channel createChannel() throws IOException, TimeoutException {

        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置rabbit主机地址
        connectionFactory.setHost("129.226.133.70");
        //默认端口
        connectionFactory.setPort(5672);
        connectionFactory.setPassword("123456");
        connectionFactory.setUsername("test");
        //创建一个新的连接
        Connection connection = connectionFactory.newConnection();
        //创建一个频道
        Channel channel = connection.createChannel();
        return channel;
    }

    public void close(){

    }
}

