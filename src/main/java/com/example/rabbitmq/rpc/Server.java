package com.example.rabbitmq.rpc;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 20:29
 */
public class Server {
    public static void main(String[] args) {
        try {
//            Thread.sleep(5000);
            Connection connection = null;
            final Channel channel = RabbitCommon.createChannel(connection);
            String rpc_queuqu = "rpc_queue";
            channel.queueDeclare(rpc_queuqu, false, false, false, null);
            channel.basicQos(1);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("服务端：已接收到请求消息：" + message);
                    // 服务器端接收到消息并处理消息
                    String response = "{'code': 200, 'data': '" + message + "'}";

                    // 将消息发布到reply_to响应队列中
                    AMQP.BasicProperties replyProperties = new AMQP.BasicProperties.Builder().
                            correlationId(properties.getCorrelationId()).build();
                    String replyTo = properties.getReplyTo();
                    channel.basicPublish("", replyTo, replyProperties, response.getBytes("UTF-8"));
                    System.out.println("服务端：请求已处理完毕，响应结果" + response + "已发送到响应队列中");
                    // 手动应答
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            // 手动应答模式
            channel.basicConsume(rpc_queuqu, false, consumer);

            System.out.println("服务端：已订阅请求队列(rpc_queue), 开始等待接收请求消息...");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

}
