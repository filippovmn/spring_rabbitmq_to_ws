package org.example.rabbitmqListener.amqp;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

//import org.springframework.messaging.handler.annotation.Header;

/**
 * Created by mfilippov on 2017-06-23.
 */
@Service
public class Listener implements ChannelAwareMessageListener {

    @Autowired
    RESTClientImpl restClient;

    Logger log= Logger.getLogger(Listener.class);

    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            log.info("message: "+new String(message.getBody()));
            if (restClient.send(new String(message.getBody()))){
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            }else{
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
        }catch(Exception e){
            log.error("error: "+new String(message.getBody()),e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }
    }







    /*public void handle2(@Payload String payload, @Header(AmqpHeaders.CHANNEL) Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws Exception {
        try {
            log.info(String.format("payload: {}",payload));
            System.out.println(String.format("payload: {}",payload));
            boolean result = restClient().send(payload.toString());
            if (!restClient().send(payload.toString())){
                channel.basicNack(deliveryTag,false,true);
            }else{
                channel.basicAck(deliveryTag,false);
            }
        } catch (Exception ex) {
            System.out.println("Custom Fail:" + ex.toString() + ";detail:" + ex.getCause());
            channel.basicNack(deliveryTag,false,true);
        }
    }*/
}