package com.ggd543.esb.component;

import com.google.gson.JsonObject;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.mule.util.UUID;

/**
 * Created by ggd543 on 14-1-15.
 */
public class BeforeExceptionComponent implements Callable {
    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {
        System.out.println("before exception " + eventContext.getMessageAsString() + " exception: " + eventContext.getMessage().getExceptionPayload().getException());
        System.out.println("root exception " + eventContext.getMessage().getExceptionPayload().getRootException());
        eventContext.getMessage().getExceptionPayload().getRootException().printStackTrace();
//        return null;
//        return new DefaultMuleMessage(eventContext.getMessage().getExceptionPayload().getException(), eventContext.getMuleContext());
        JsonObject json = new JsonObject();
        json.addProperty("name", "archer");
        json.addProperty("uuid", UUID.getUUID());
        return new DefaultMuleMessage(json, eventContext.getMuleContext());
    }
}
