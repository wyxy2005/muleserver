package com.ucweb.esb.component;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ucweb.esb.processor.ExceptionProcessor;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

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
public class ExceptionHandler implements Callable {
    private ExceptionProcessor exceptionProcessor = new ExceptionProcessor() {
        @Override
        public Object process(Throwable cause) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            cause.printStackTrace(pw);
            return sw.getBuffer().toString();
        }
    };

    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {
        Throwable cause = eventContext.getMessage().getExceptionPayload().getRootException();
        return exceptionProcessor.process(cause);
    }

    public ExceptionProcessor getExceptionProcessor() {
        return exceptionProcessor;
    }

    public void setExceptionProcessor(ExceptionProcessor exceptionProcessor) {
        this.exceptionProcessor = exceptionProcessor;
    }
}
