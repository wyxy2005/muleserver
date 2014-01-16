package com.ggd543.esb.component;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

/**
 * Created by ggd543 on 14-1-15.
 */
public class BeforeExceptionComponent implements Callable {
    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {
        System.out.println("before exception " + eventContext.getMessageAsString() + " exception: " + eventContext.getMessage().getExceptionPayload().getException());
        System.out.println("root exception "+eventContext.getMessage().getExceptionPayload().getRootException());
        eventContext.getMessage().getExceptionPayload().getRootException().printStackTrace();
        return null;
    }
}
