/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.rabbitmqListener.amqp;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.client.RestTemplate;

import com.rabbitmq.client.Channel;

import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Configuration
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ComponentScan
public class SampleAmqpSimpleApplication extends SpringBootServletInitializer  {
	
	 Log log=LogFactory.getLog(SampleAmqpSimpleApplication.class);
	 @Value("${spring.rabbitmq.host}")
	 private String host;
	 @Value("${spring.rabbitmq.listener.queue}")
	 private String queue;
	 
	 @Override
	 protected SpringApplicationBuilder configure (SpringApplicationBuilder builder) {
	     return builder.sources(SampleAmqpSimpleApplication.class);
	 }

	@SuppressWarnings("unused")
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	public ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor() {
		return new ScheduledAnnotationBeanPostProcessor();
	}

	@Bean
	public Sender mySender() {
		return new Sender();
	}
	@Autowired
	protected RESTClientImpl restClient;
	
	@Autowired
	public WebServiceClient client;
	
	@Bean
	public WebServiceClient client(){
		return new WebServiceClient();
	}
	
	@Bean
	public SimpleMessageListenerContainer container() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
				this.connectionFactory);
		MessageListener listener = new MessageListener() {
			//@SuppressWarnings("unused")
			public void onMessage(Message message) {
				//System.out.println(new String(message));
				//StreamResult result=client.customSendAndReceive(new String(message));
				//if (result.toString().toLowerCase().contains("fault")){channel.basicNack(0, false, false);}
				//System.out.println(result.toString());
				try{
					String body=new String(message.getBody());
					String id=new String(message.getMessageProperties().getHeaders().get("io_id").toString());
					String xml=String.format("<request><message>%s</message><i_o_id>%s</i_o_id></request>",body,id);					
					boolean result=restClient.send(xml);
				}
				catch(Exception ex){
					System.out.println("Custom Fail:"+ex.toString()+";detail:"+ex.getCause());
				}
			}
		};
		MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
		container.setMessageListener(adapter);
		container.setQueueNames(queue);
		return container;
	}
	

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleAmqpSimpleApplication.class, args);
	}
}
