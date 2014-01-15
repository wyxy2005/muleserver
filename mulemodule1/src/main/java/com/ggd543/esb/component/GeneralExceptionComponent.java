package com.ggd543.esb.component;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

/**
 * Created by ggd543 on 14-1-15.
 */
public class GeneralExceptionComponent  implements Callable {
    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {
        System.out.println("general exception "+eventContext.getMessageAsString());
        return eventContext.getMessage();
    }
}
