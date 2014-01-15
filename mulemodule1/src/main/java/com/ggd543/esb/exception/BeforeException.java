package com.ggd543.esb.exception;

import java.util.List;

import org.mule.api.MuleException;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
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
public class BeforeException extends MuleException {
    public BeforeException(Message message) {
        super(message);
    }

    public BeforeException(Message message, Throwable cause) {
        super(message, cause);
    }

    public BeforeException(Throwable cause) {
        super(cause);
    }

    public BeforeException() {
    }
}
