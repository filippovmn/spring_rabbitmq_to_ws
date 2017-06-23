package org.example.rabbitmqListener.amqp;

import org.apache.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

public class RESTClientImpl extends RestTemplate {

	Logger log= Logger.getLogger(RESTClientImpl.class);

	public String getUrl() {
		return url;
	}

	/*public void setUrl(String url) {
		this.url = url;
	}*/

	private String url;

	public RESTClientImpl(String url){
		log.info("try to init request factory");
		this.url=url;
		ClientHttpRequestFactory factory= this.getRequestFactory();
		try {
			factory.createRequest(new URI(this.url), HttpMethod.POST);
		} catch (IOException e) {
			log.error("Error in sending.",e);
		} catch (URISyntaxException e) {
			log.error("Error in sending.",e);
		}
		this.setRequestFactory(factory);
	}
	
	boolean send(String message){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<String> payload = new HttpEntity<String>(message,headers);
		ResponseEntity<String> response=this.exchange(url,HttpMethod.POST,payload,String.class);
		java.util.Date date=new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		System.out.println("start:"+dateFormat.format(date.getTime()));
		
		if(response.getStatusCode()==HttpStatus.OK&&!response.getBody().toLowerCase().contains("fault")){
			System.out.println("end:"+dateFormat.format(date.getTime()));
		};
		return false;
	}
	
}
