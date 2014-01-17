package com.ucweb.esb.processor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ucweb.esb.exception.DataProcessorException;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月17
 */
public class JsonProcessor extends DataProcessor {
    @Override
    public Object before(Object src, String enc) throws DataProcessorException {
        System.out.println(getClass().getName() + "before ");
        JsonObject json = new JsonObject();
        for (String token : ((String) src).split("&")) {
            String[] kv = token.split("=");
            if (kv != null && kv.length == 2) {
                json.addProperty(kv[0], kv[1]);
            }
        }
        System.out.println("transform " + src + " to " + json.toString());
        return json.toString();
    }

    @Override
    public Object after(Object src, String enc) throws DataProcessorException {
        System.out.println(getClass().getName()+" after ");
        JsonObject json = new JsonParser().parse((String) src).getAsJsonObject();
        json.addProperty("timestamp", System.currentTimeMillis());
        return json.toString();
    }

}
