package org.example.rabbitmqListener.amqp;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by mfilippov on 2017-06-19.
 */
public class application {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();
        ctx.register(AMQPListenerConfig.class);
        ctx.refresh();
    }
}
