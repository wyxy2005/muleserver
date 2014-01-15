package com.ucweb.esb.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月10
 */
@Path("/helloworld")
public class HelloWorldResource {
    @GET
//    @Produces({"text/plain", "application/json"})
    @Path("/{name}")
    public String sayHello(@PathParam("name") String name) {
        return "Hello " + name;
    }
}
