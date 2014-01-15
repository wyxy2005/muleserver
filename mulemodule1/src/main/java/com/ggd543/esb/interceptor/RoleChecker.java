package com.ggd543.esb.interceptor;

import com.ggd543.esb.exception.AfterException;
import com.ggd543.esb.exception.BeforeException;
import com.ggd543.esb.exception.GeneralException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.transformer.TransformerException;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.interceptor.AbstractEnvelopeInterceptor;
import org.mule.management.stats.ProcessingTime;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月15
 */
public class RoleChecker extends AbstractEnvelopeInterceptor {

    @Override
    public MuleEvent before(MuleEvent event) throws MuleException {

        try {
            MuleMessage message = event.getMessage();
            System.out.println("role checker before : " + this + " message: " + message.getPayloadAsString());
            System.out.println(" ~~~~ inbound properties ~~~~");
            for(String name: message.getInboundPropertyNames()){
                System.out.println(name+" : "+ message.getInboundProperty(name));
            }
            System.out.println("---- outbound properties --- ");
            for(String name: message.getOutboundPropertyNames()){
                System.out.println(name+" : "+ message.getOutboundProperty(name));
            }

            System.out.println("==== session properties  ==== ");
            for(String name: message.getSessionPropertyNames()){
                System.out.println(name+" : "+ message.getSessionProperty(name));
            }
//            HttpGet getReq = new HttpGet("http://127.0.0.1:9000/service/check");
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpResponse httpResponse = httpClient.execute(getReq);
//            HttpEntity entity = httpResponse.getEntity();
//            String msg = IOUtils.toString(entity.getContent());
//            System.out.println("============ "+msg);
//            if (!StringUtils.equals("SUCCESS", msg)){
//                System.out.println("not work");
//                throw new BeforeException();
//            }
//            if ((int) (Math.random() * 2) == 0) {
//                System.out.println("throw an exception on BeforeAction ");
//
//                throw new BeforeException();
//            }
        } catch (Exception e) {
            throw new BeforeException(e.getCause());
        }
        return event;
    }

    @Override
    public MuleEvent after(MuleEvent event) throws MuleException {
        try {
            System.out.println("role checker after : " + this + " message: " + event.getMessage().getPayloadAsString());
//            if ((int) (Math.random() * 2) == 0) {
//                System.out.println("throw an exception on AfterAction");
//                throw new AfterException();
//            }
        } catch (Exception e) {
            throw new AfterException();
        }
        return event;
    }

    @Override
    public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {
//        if ((int) (Math.random() * 2) == 0) {
//            System.out.println("throw an exception on AfterAction");
//            throw new GeneralException();
//        }
        System.out.println("role checker last : " + this + " - accumulator: " + time.getAccumulator() );
        return event;
//        try {
//            System.out.println("role checker last : " + this + " - accumulator: " + time.getAccumulator() + " statistics: " + time.getStatistics()
//                    + time.getAccumulator() + " exceptionWasThrown: " + exceptionWasThrown + " message: " + event.getMessage().getPayloadAsString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (exceptionWasThrown) {
//            MuleMessage message = new DefaultMuleMessage("an exception occur", event.getMuleContext());
//            message.setPayload("an exception");
////            event.setMessage(message);
//            event = new DefaultMuleEvent(message, event);
//        }
//        return event;
    }
}
