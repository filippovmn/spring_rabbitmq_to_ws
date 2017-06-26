package org.example.rabbitmqListener.amqp;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
        }
    }
}
