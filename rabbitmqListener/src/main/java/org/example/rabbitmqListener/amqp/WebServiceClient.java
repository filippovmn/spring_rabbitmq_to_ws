package org.example.rabbitmqListener.amqp;
import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
//import org.springframework.ws.transport.WebServiceMessageSender;

public class WebServiceClient {

    private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
    
    public void setDefaultUri(String defaultUri) {
        webServiceTemplate.setDefaultUri(defaultUri);
    }
    @Value("${spring.ws.uri}")
    private String uri;
    @Value("${spring.ws.soapaction}")
    private String soapAction;
    

    // send to the configured default URI
    public void simpleSendAndReceive(String message) {
        StreamSource source = new StreamSource(new StringReader(message));
        StreamResult result = new StreamResult(System.out);
        webServiceTemplate.sendSourceAndReceiveToResult(source, result);
    }

    // send to an explicit URI
    public StreamResult customSendAndReceive(String message) {
    	System.out.println(message+" "+uri+" "+soapAction);
        StreamSource source = new StreamSource(new StringReader(message));
        StreamResult result = new StreamResult(System.out);
        SoapActionCallback callback=new SoapActionCallback(soapAction);
        boolean ansver=webServiceTemplate.sendSourceAndReceiveToResult(
        		 uri,
                 source,
                 callback,
                 result);
        //if (ansver==false){result=new StreamResult("fault");};
        return result;
    }

}
