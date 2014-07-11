package backtype.storm.messaging.netty;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class StormKeyStore {

    private StormKeyStore() {}

    public static KeyStore get() throws Exception {
	InputStream keyStoreStream = null;
	try {
	    keyStoreStream = getInputStream();
	    KeyStore ks = KeyStore.getInstance("JKS");
	    ks.load(keyStoreStream, getKeyStorePassword());
	    return ks;
	} finally {
	    if (keyStoreStream != null) {
		keyStoreStream.close();
	    }
	}
    }
    
    static InputStream getInputStream() throws Exception {
	String keystoreLocation =
	    System.getProperty("storm.keystore.file");
	return new FileInputStream(keystoreLocation);
    }

    static char[] getCertificatePassword() {
	return System.getProperty("storm.certificate.password").toCharArray();
    }

    static char[] getKeyStorePassword() {
	return System.getProperty("storm.keystore.password").toCharArray();
    }
}
