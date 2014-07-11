package backtype.storm.messaging.netty;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

public class StormTrustManagerFactory extends TrustManagerFactorySpi {

    private static final TrustManager TRUST_MANAGER = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType)
                throws CertificateException {
            // Always trust.
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType)
                throws CertificateException {
            // Always trust.
        }
    };

    public static TrustManager[] getTrustManagers() {
        return new TrustManager[] { TRUST_MANAGER };
    }

    protected TrustManager[] engineGetTrustManagers() {
        return getTrustManagers();
    }

    protected void engineInit(KeyStore keystore) throws KeyStoreException {
        // Unused.
    }

    protected void engineInit(ManagerFactoryParameters ManagerFactoryParameters)
            throws InvalidAlgorithmParameterException {
        // Unused.
    }
}
