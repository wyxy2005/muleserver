package com.ggd543.esb.interceptor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.interceptor.Interceptor;
import org.mule.api.processor.MessageProcessor;

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
public class PermissionChecker implements Interceptor {
    @Override
    public MuleEvent process(MuleEvent event) throws MuleException {
        System.out.println("permission checker");
        return event;
    }

    public final MessageProcessor getListener() {
        return next;
    }

    @Override
    public void setListener(MessageProcessor next) {
        System.out.println("set messageProccessor for PermissionChecker : "+next);
        this.next = next;
    }

    protected MessageProcessor next;
}
