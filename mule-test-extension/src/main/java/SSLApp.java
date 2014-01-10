import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月09
 */
public class SSLApp {
    private static HttpURLConnection getConnection(URL url, String method)
            throws IOException {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        boolean isSecure ="https".equalsIgnoreCase(url.getProtocol());
        if (isSecure) {
            ((HttpsURLConnection)conn).setSSLSocketFactory(TrustManager.createSSLSocketFactory());
            ((HttpsURLConnection)conn).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
//        conn.setRequestProperty("User-Agent", "ucportal-sdk-java");
        // conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }

    public static void main(String[] args) throws IOException {
        int connectTimeout = 60000;
        int readTimeout = 60000;
        HttpURLConnection conn = getConnection(new URL("https://yftest.ucweb.local:4445"), "GET");
        conn.connect();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        InputStream is = conn.getInputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        StringBuilder sb = new StringBuilder();
        while((len = is.read(buf ,0 ,buf.length)) != -1){
            sb.append(new String(buf, 0, len));
        }
        is.close();
        System.out.println(sb.toString());
    }
}
