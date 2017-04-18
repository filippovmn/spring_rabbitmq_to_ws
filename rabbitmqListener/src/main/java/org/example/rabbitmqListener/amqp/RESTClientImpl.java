package org.example.rabbitmqListener.amqp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RESTClientImpl extends RestTemplate {
	
	@Value("${spring.rest.uri}")
	private String url;
	
	{
		ClientHttpRequestFactory factory= this.getRequestFactory();
		try {
			factory.createRequest(new URI(this.url), HttpMethod.POST);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setRequestFactory(factory);
		
	}
	
	boolean send(String message){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		HttpEntity<String> payload = new HttpEntity<String>(message,headers);
		ResponseEntity<String> response=this.exchange("http://localhost:8281/rabbitMessage/add",HttpMethod.POST,payload,String.class);
		/*this.setRequestFactory(new ClientHttpRequestFactory() {
			
			@Override
			public ClientHttpRequest createRequest(URI arg0, HttpMethod arg1) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
		});*/
		java.util.Date date=new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		System.out.println("start:"+dateFormat.format(date.getTime()));
		
		if(response.getStatusCode()==HttpStatus.OK&&!response.getBody().toLowerCase().contains("fault")){
			System.out.println("end:"+dateFormat.format(date.getTime()));
			return true;
		};
		return false;
	}
	
}
