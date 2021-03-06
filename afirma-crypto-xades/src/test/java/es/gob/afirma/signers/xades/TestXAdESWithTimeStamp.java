package es.gob.afirma.signers.xades;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.Properties;

import org.junit.Test;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;

public class TestXAdESWithTimeStamp {

	private static final String CERT_PATH = "PruebaEmpleado4Activo.p12"; //$NON-NLS-1$
    private static final String CERT_PASS = "Giss2016"; //$NON-NLS-1$
    private static final String CERT_ALIAS = "givenname=prueba4empn+serialnumber=idces-00000000t+sn=p4empape1 p4empape2 - 00000000t+cn=prueba4empn p4empape1 p4empape2 - 00000000t,ou=personales,ou=certificado electronico de empleado publico,o=secretaria de estado de la seguridad social,c=es"; //$NON-NLS-1$

	private static final String CATCERT_POLICY = "0.4.0.2023.1.1"; //$NON-NLS-1$
	private static final String CATCERT_TSP = "http://psis.catcert.net/psis/catcert/tsp"; //$NON-NLS-1$
	private static final Boolean CATCERT_REQUIRECERT = Boolean.TRUE;

    private static final Properties EXTRAPARAMS = new Properties();
    static {
	    EXTRAPARAMS.put("tsaURL", CATCERT_TSP); //$NON-NLS-1$
	    EXTRAPARAMS.put("tsaPolicy", CATCERT_POLICY); //$NON-NLS-1$
	    EXTRAPARAMS.put("tsaRequireCert", CATCERT_REQUIRECERT); //$NON-NLS-1$
	    EXTRAPARAMS.put("tsaHashAlgorithm", "SHA-512"); //$NON-NLS-1$ //$NON-NLS-2$
	    EXTRAPARAMS.put("tsType", TsaParams.TS_SIGN_DOC); //$NON-NLS-1$
    }

    @Test
    public void testXAdEST() throws Exception {

    	final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
        ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS.toCharArray());

        final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(
    		CERT_ALIAS,
    		new KeyStore.PasswordProtection(CERT_PASS.toCharArray())
		);

    	final byte[] data = AOUtil.getDataFromInputStream(ClassLoader.getSystemResourceAsStream("xml_with_ids.xml")); //$NON-NLS-1$

    	final Properties extraParams = new Properties(EXTRAPARAMS);

    	final AOXAdESSigner signer = new AOXAdESSigner();
    	final byte[] signature = signer.sign(
    			data,
    			AOSignConstants.SIGN_ALGORITHM_SHA512WITHRSA,
    			pke.getPrivateKey(),
    			pke.getCertificateChain(),
    			extraParams);


    	final File tempFile = File.createTempFile("XAdES-T_", ".xml");
    	try (FileOutputStream fos = new FileOutputStream(tempFile);) {
    		fos.write(signature);
    	}

    	System.out.println("La firma XAdES-T se ha guardado en: " + tempFile.getAbsolutePath());
    }

    @Test
    public void testXAdESTLevel() throws Exception {

    	final KeyStore ks = KeyStore.getInstance("PKCS12"); //$NON-NLS-1$
        ks.load(ClassLoader.getSystemResourceAsStream(CERT_PATH), CERT_PASS.toCharArray());

        final PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(
    		CERT_ALIAS,
    		new KeyStore.PasswordProtection(CERT_PASS.toCharArray())
		);

    	final byte[] data = AOUtil.getDataFromInputStream(ClassLoader.getSystemResourceAsStream("xml_with_ids.xml")); //$NON-NLS-1$

    	final Properties extraParams = new Properties(EXTRAPARAMS);
    	extraParams.setProperty("profile", "B-B-Level");

    	final AOXAdESSigner signer = new AOXAdESSigner();
    	final byte[] signature = signer.sign(
    			data,
    			AOSignConstants.SIGN_ALGORITHM_SHA512WITHRSA,
    			pke.getPrivateKey(),
    			pke.getCertificateChain(),
    			extraParams);


    	final File tempFile = File.createTempFile("XAdES-T-Level_", ".xml");
    	try (FileOutputStream fos = new FileOutputStream(tempFile);) {
    		fos.write(signature);
    	}

    	System.out.println("La firma XAdES-T-Level se ha guardado en: " + tempFile.getAbsolutePath());
    }
}
