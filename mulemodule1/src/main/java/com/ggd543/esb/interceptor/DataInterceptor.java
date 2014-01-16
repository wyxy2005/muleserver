package com.ggd543.esb.interceptor;

import com.ggd543.esb.exception.AfterException;
import com.ggd543.esb.exception.BeforeException;
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
            if ((int) (Math.random() * 2) == 0) {
                System.out.println("throw before exception");
                throw new BeforeException();
            }
            message.setPayload("iiiiiiiiiiiiiiiiiiiii");
        }catch (MuleException e ){
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultMuleEvent(message, event);
    }

    @Override
    public MuleEvent after(MuleEvent event) throws MuleException {
        MuleMessage message = event.getMessage();
        try {
            if ((int) (Math.random() * 2) == 0) {
                System.out.println("throw after exception");
                throw new AfterException();
            }
            System.out.println("after interceptor: "+message.getPayloadAsString());
            message.setPayload(new Date() + "_" + message.getPayloadAsString());
        }catch (MuleException e ){
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultMuleEvent(message, event);
    }

    @Override
    public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {
//        MuleMessage message = event.getMessage();
        System.out.println("last");
        return event;
    }
}
