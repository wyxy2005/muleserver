package com.ucweb.esb.transformer;

import com.ucweb.esb.exception.DataTransformerException;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月07
 */
@Deprecated
public interface DataTransformer {
    public Object doTransform(Object src, String enc) throws DataTransformerException;
}

