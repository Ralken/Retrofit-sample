package cn.ralken.android.http;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Helper class that loads .ca certificate file into stream, use HTTPS connection with customized CA certification.
 *
 * Created by Ralken Liao on 01/11/2017.
 */

final class CertificateDecoder {

    private CertificateDecoder(){
    }

    static SSLSocketFactory getSslSocketFactory(InputStream caInputStream) throws Exception{
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt

        InputStream caInput = new BufferedInputStream(caInputStream);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = ignoreExpiredCertificate(tmf);

        // Create an SSLContext that uses our TrustManager

        // SSLContext context = SSLContext.getInstance("TLS");

        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(null, wrappedTrustManagers, null);

        SSLSocketFactory noSSLv3Factory = new NoSSLv3SocketFactory(context.getSocketFactory());
        HttpsURLConnection.setDefaultSSLSocketFactory(noSSLv3Factory);

        return context.getSocketFactory();
    }

    private static TrustManager[] ignoreExpiredCertificate(TrustManagerFactory tmf) {
        TrustManager[] trustManagers = tmf.getTrustManagers();
        final X509TrustManager origTrustmanager = (X509TrustManager) trustManagers[0];

        TrustManager[] wrappedTrustManagers = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return origTrustmanager.getAcceptedIssuers();
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                origTrustmanager.checkClientTrusted(chain, authType);
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    origTrustmanager.checkServerTrusted(chain, authType);
                } catch (Exception e) {
                    // Ignored java.security.cert.CertificateExpiredException
                }
            }
        } };

        return wrappedTrustManagers;
    }

}