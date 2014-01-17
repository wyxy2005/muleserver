package com.ggd543.esb.component;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.gson.JsonObject;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

/**
 * Created by ggd543 on 14-1-15.
 */
public class GeneralExceptionComponent implements Callable {
    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {
        System.out.println("general exception " + eventContext.getMessage().getExceptionPayload());
        Throwable e = eventContext.getMessage().getExceptionPayload().getRootException();
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
