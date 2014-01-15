package com.ggd543.esb.interceptor;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.interceptor.AbstractEnvelopeInterceptor;
import org.mule.management.stats.ProcessingTime;

import java.util.Date;

/**
 * Created by ggd543 on 14-1-15.
 */
public class DataInterceptor extends AbstractEnvelopeInterceptor {
    @Override
    public MuleEvent before(MuleEvent event) throws MuleException {
        MuleMessage message = event.getMessage();
        try {
            System.out.println("before interceptor: "+message.getPayloadAsString());
            message.setPayload("iiiiiiiiiiiiiiiiiiiii");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultMuleEvent(message, event);
    }

    @Override
    public MuleEvent after(MuleEvent event) throws MuleException {
        MuleMessage message = event.getMessage();
        try {
            System.out.println("after interceptor: "+message.getPayloadAsString());
            message.setPayload(new Date() + "_" + message.getPayloadAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultMuleEvent(message, event);
    }

    @Override
    public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {
//        MuleMessage message = event.getMessage();
        return event;
    }
}
