/**
 *
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月15
 */
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

//println(payload)
//println(payload.getPath())
//println(payload.getPort())
//println(payload.getHost())
prevPayLoad = payload
HttpGet getReq = new HttpGet(serviceMonitorUrl);
HttpClient httpClient = new DefaultHttpClient();
HttpResponse httpResponse = httpClient.execute(getReq);
HttpEntity entity = httpResponse.getEntity();
String msg = IOUtils.toString(entity.getContent());
System.out.println(" #### "+msg+" ####");
if (!StringUtils.equals("SUCCESS", msg)){
    System.out.println("not work");
    throw new com.ggd543.esb.exception.InvalidServiceExcepton();
}
return prevPayLoad