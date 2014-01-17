package com.ucweb.esb.interceptor;

import com.ucweb.esb.exception.DataProcessorException;
import com.ucweb.esb.exception.InterceptorException;
import com.ucweb.esb.processor.DataProcessor;
import org.mule.DefaultMuleEvent;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
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
 * create on: 2014年01月16
 */
public class ESBInterceptor extends AbstractEnvelopeInterceptor {

    private DataProcessor processor = new DataProcessor() {
        @Override
        public Object before(Object src, String enc) throws DataProcessorException {
            return src;
        }

        @Override
        public Object after(Object src, String enc) throws DataProcessorException {
            return src;
        }

        @Override
        public Object last(Object src, String enc, boolean exceptionHasThrown, Throwable cause) throws DataProcessorException {
            return src;
        }
    };


    @Override
    public MuleEvent before(MuleEvent event) throws MuleException {
        System.out.println(getClass().getName() + " before ");
        MuleMessage message = event.getMessage();
        try {
            Object responsePayload = processor.before(message.getPayload(), message.getEncoding());
            message.setPayload(responsePayload);
            return new DefaultMuleEvent(message, event);
        } catch (DataProcessorException e) {
            throw new InterceptorException(e);
        }
    }

    @Override
    public MuleEvent after(MuleEvent event) throws MuleException {
        System.out.println(getClass().getName() + " after ");
        MuleMessage message = event.getMessage();
        try {
            Object responsePayload = processor.after(message.getPayload(), message.getEncoding());
            message.setPayload(responsePayload);
            return new DefaultMuleEvent(message, event);
        } catch (DataProcessorException e) {
            throw new InterceptorException(e);
        }
    }

    @Override
    public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {
        System.out.println(getClass().getName() + "  last " + processor + " " + event.getMessage().getPayload());
        MuleMessage message = event.getMessage();
        try {
            ExceptionPayload exceptionPayload = message.getExceptionPayload();
            if (exceptionPayload != null){
                Object responsePayload = processor.last(message.getPayload(), message.getEncoding(),
                        exceptionWasThrown, exceptionPayload.getRootException());
                message.setPayload(responsePayload);
            }
            return new DefaultMuleEvent(message, event);
        } catch (DataProcessorException e) {
            throw new InterceptorException(e);
        }
    }

    public DataProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(DataProcessor processor) {
        this.processor = processor;
    }
}
