package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.User;
import play.Logger;
import play.mvc.Controller;

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
public class TestController extends Controller {
    public static void logJson(String body) {
        JsonObject json = new JsonParser().parse(body).getAsJsonObject();
        Logger.info("receive: %s", json.toString());
        User user = new User();
        user.username = "liuyj3";
        user.email = "liuyj3@ucweb.com";
        renderJSON(user);
    }


}
