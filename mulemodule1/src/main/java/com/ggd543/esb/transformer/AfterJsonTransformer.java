package com.ggd543.esb.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ucweb.esb.transformer.DataTransformer;
import com.ucweb.esb.transformer.DataTransformerException;

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
public class AfterJsonTransformer implements DataTransformer {
    @Override
    public Object doTransform(Object src, String enc) throws DataTransformerException {
        JsonObject json = new Gson().toJsonTree(src).getAsJsonObject();
        json.addProperty("timestamp", System.currentTimeMillis());
        return json.toString();
    }
}
