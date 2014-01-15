package com.ggd543.esb.exception;

import org.mule.api.MuleException;
import org.mule.config.i18n.Message;

/**
 * Created by ggd543 on 14-1-16.
 */
public class InvalidServiceExcepton extends MuleException {
    public InvalidServiceExcepton(Message message) {
        super(message);
    }

    public InvalidServiceExcepton(Message message, Throwable cause) {
        super(message, cause);
    }

    public InvalidServiceExcepton(Throwable cause) {
        super(cause);
    }

    public InvalidServiceExcepton() {
    }
}

