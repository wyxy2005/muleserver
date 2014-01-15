package com.ggd543.esb.exception;

import org.mule.api.MuleException;
import org.mule.config.i18n.Message;

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
public class GeneralException extends MuleException {
    public GeneralException(Message message) {
        super(message);
    }

    public GeneralException(Message message, Throwable cause) {
        super(message, cause);
    }

    public GeneralException(Throwable cause) {
        super(cause);
    }

    public GeneralException() {
    }
}
