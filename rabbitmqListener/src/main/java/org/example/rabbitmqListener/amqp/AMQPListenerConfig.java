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

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.conf")
public class AMQPListenerConfig {
	
	 Logger log= Logger.getLogger(AMQPListenerConfig.class);

	 @Value("${spring.rabbitmq.host}")
	 private String host;
	 @Value("${spring.rabbitmq.port}")
	 private String port;
	 @Value("${spring.rabbitmq.virtual-host}")
	 private String virtual_host;
	 @Value("${spring.rabbitmq.username}")
	 private String username;
	 @Value("${spring.rabbitmq.password}")
	 private String password;
	 @Value("${spring.rabbitmq.listener.queue}")
	 private String queue;

	 @Value("${spring.rest.uri}")
	 private String url;

	@Bean
	ConnectionFactory connectionFactory(){
		CachingConnectionFactory factory =  new  CachingConnectionFactory(host,Integer.parseInt(port));
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setVirtualHost(virtual_host);
		return factory;
	};

	@Bean
	protected RESTClientImpl restClient(){
		RESTClientImpl client = new RESTClientImpl(url);
		return client;
	};

	@Bean
	public RabbitAdmin rabbitAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		return rabbitAdmin;
	}

	@Bean
	public Listener listener(){
		return new Listener();
	}

	@Bean
	public Queue queue() {
		return new Queue(queue);
	}

	@Bean
	public SimpleMessageListenerContainer container() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
				this.connectionFactory());
		container.setAutoDeclare(true);
		container.setRabbitAdmin(rabbitAdmin());
		container.setQueueNames(queue);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		MessageListenerAdapter adapter = new MessageListenerAdapter(listener());
		container.setMessageListener(adapter);
		return container;
	};

	@Override
	public String toString() {
		return "AMQPListenerConfig{" +
				"host='" + host + '\'' +
				", port='" + port + '\'' +
				", virtual_host='" + virtual_host + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", queue='" + queue + '\'' +
				", target url='" + url + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AMQPListenerConfig that = (AMQPListenerConfig) o;

		if (host != null ? !host.equals(that.host) : that.host != null) return false;
		if (port != null ? !port.equals(that.port) : that.port != null) return false;
		if (virtual_host != null ? !virtual_host.equals(that.virtual_host) : that.virtual_host != null) return false;
		if (username != null ? !username.equals(that.username) : that.username != null) return false;
		if (password != null ? !password.equals(that.password) : that.password != null) return false;
		if (queue != null ? !queue.equals(that.queue) : that.queue != null) return false;
		return url != null ? url.equals(that.url) : that.url == null;
	}

	@Override
	public int hashCode() {
		int result = host != null ? host.hashCode() : 0;
		result = 31 * result + (port != null ? port.hashCode() : 0);
		result = 31 * result + (virtual_host != null ? virtual_host.hashCode() : 0);
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (queue != null ? queue.hashCode() : 0);
		result = 31 * result + (url != null ? url.hashCode() : 0);
		return result;
	}

	public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {

	}
}
