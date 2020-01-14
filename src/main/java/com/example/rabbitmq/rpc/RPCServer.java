package com.example.rabbitmq.rpc;

import com.example.rabbitmq.common.RabbitCommon;
import com.rabbitmq.client.*;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author YM
 * @date 2020/1/13 17:01
 */
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return fib(n - 1) + fib(n - 2);
    }

    private static void excute() throws IOException, TimeoutException, InterruptedException {
        Connection connection = null;
        Channel channel = RabbitCommon.createChannel(connection);

        //生命队列
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        //声明数量
        channel.basicQos(1);

        System.out.println("RPCServer [x] Awaiting RPC requests");
        //获取用户
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().
                        correlationId(properties.getCorrelationId()).build();
                String response = null;
                try {
                    //传递过来的内容
                    String message = new String(body, StandardCharsets.UTF_8);
                    int n = Integer.parseInt(message);
                    System.out.println("RPCServer [.] fib(" + message + ")");
                    //调用
                    response = "" + fib(n);
                } catch (Exception e) {
                    System.out.println(" [.] " + e.toString());
                    response = "";
                } finally {
                    //向回调队列发送结果信息
                    channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
                    //处理完后  发送消息确认
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
//                RabbitCommon.close(connection, channel);
            }
        };
        //采用应答模式监听，处理完后才从请求队列中删除请求
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
    }

    public static void main(String[] args) {
        try {
            excute();
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
