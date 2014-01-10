


import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    // getAcceptedIssuers retrieves all of the certificates in the keyStore
    // and returns them in an X509Certificate array.
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public static SSLSocketFactory createSSLSocketFactory() {
        TrustManager[] tms = {new TrustManager()};

        // Create context based on a TLS protocol and a SunJSSE provider.
//            SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, tms, new SecureRandom());
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }

    }
}