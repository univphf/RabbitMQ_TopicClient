package com.ht.dev.topicc;

import com.rabbitmq.client.*;
import java.io.IOException;

public class ReceiveLogsTopic {

  private static final String EXCHANGE_NAME = "topic_logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    //Declarer un Exchange de type TOPIC
    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
    //declarer un file d'attente et récuperer son nom
    String queueName = channel.queueDeclare().getQueue();

    if (argv.length < 1)
    {
      System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
      System.exit(1);
    }

    for (String bindingKey : argv) {channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);}

    System.out.println(" [*] En attente de messages.CTRL+C pour quitter");

    Consumer consumer = new DefaultConsumer(channel)
    {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,AMQP.BasicProperties properties, byte[] body) throws IOException
      {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Reçu '" + envelope.getRoutingKey() + "':'" + message + "'");
      }
    };

    channel.basicConsume(queueName, true, consumer);
  }
}

