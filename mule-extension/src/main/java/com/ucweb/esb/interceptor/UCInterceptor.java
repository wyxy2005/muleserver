package com.ucweb.esb.interceptor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
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
public class UCInterceptor extends AbstractEnvelopeInterceptor {
//    protected Object
    @Override
    public MuleEvent before(MuleEvent event) throws MuleException {
//        event.getMessage().getpay
    }

    @Override
    public MuleEvent after(MuleEvent event) throws MuleException {
        return null;
    }

    @Override
    public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {
        return null;
    }
}
