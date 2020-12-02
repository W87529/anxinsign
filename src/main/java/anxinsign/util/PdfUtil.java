package anxinsign.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.com.itextpdf.kernel.color.Color;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.seal.base.bean.appearance.AbstractAppearance;
import cfca.sadk.seal.base.bean.appearance.AbstractAppearance.GSBlendMode;
import cfca.sadk.seal.base.bean.appearance.BlankAppearance;
import cfca.sadk.seal.base.bean.appearance.GraphicAppearance;
import cfca.sadk.seal.base.bean.sign.SealResultInfo;
import cfca.sadk.seal.base.bean.sign.SignInfoConfig;
import cfca.sadk.seal.base.bean.sign.SignInfoConfig.SignatureVersion;
import cfca.sadk.seal.base.exception.SealException;
import cfca.sadk.seal.base.external.AbstractExternalP7Signer;
import cfca.sadk.seal.base.util.AsynCFCASigner;
import cfca.sadk.seal.base.util.CFCAP7Signer;
import cfca.sadk.seal.base.util.FontUtil;
import cfca.sadk.seal.util.AsyncSignatureUtil;
import cfca.sadk.system.Mechanisms;
import cfca.sadk.util.Base64;
import cfca.sadk.x509.certificate.X509Cert;
import anxinsign.vo.ReservedPdfVO;

public class PdfUtil {
    private static final String REASON = "电子签名";
    private static final String LOCATION = "北京";
    private static final float TRANSPARENCY = 0.8f;

    private static Session session = null;
    static {
        try {
            String deviceName = JCrypto.JSOFT_LIB;
            JCrypto.getInstance().initialize(deviceName, null);
            session = JCrypto.getInstance().openSession(deviceName);

            FontUtil.getInstance("./ttf/simsun_new.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ReservedPdfVO asyncSignature(byte[] pdfBytes, X509Cert x509Cert, byte[] imageBytes, String signText, Color color, String signLocationField)
            throws Exception {
        try {
        	ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AbstractAppearance appearance = getBlankAppearance(imageBytes, signText, color, signLocationField);
            SignInfoConfig signInfo = getSignInfoConfigAsyn(x509Cert);
            signInfo.setP7Signer(new CFCAP7Signer(signInfo));

            AsyncSignatureUtil.createBlankSignature(bais, baos, appearance, signInfo);

            ReservedPdfVO reservedPdf = new ReservedPdfVO();
            reservedPdf.setPdfData(baos.toByteArray());
            reservedPdf.setHashValue(Base64.toBase64String(((CFCAP7Signer) signInfo.getP7Signer()).getHashValue()));
            return reservedPdf;
        } catch (Exception e) {
            throw e;
        }
    }

    public static byte[] combineExternalSignature(byte[] pdfBytes, byte[] signedBytes, X509Cert x509Cert, String signLocationField) throws Exception {
        try {
            AbstractExternalP7Signer asynCFCASigner;
            if (x509Cert.isSM2Cert()) {
                asynCFCASigner = new AsynCFCASigner(signedBytes, Mechanisms.M_SM3_SM2);
            } else {
                asynCFCASigner = new AsynCFCASigner(signedBytes, Mechanisms.M_SHA256_RSA);
            }

            GraphicAppearance appearance = new GraphicAppearance();
            appearance.setSignFieldName(signLocationField);
            SealResultInfo result = AsyncSignatureUtil.combineExternalP7Signature(pdfBytes, appearance, asynCFCASigner);
            return result.getSignedPDFData();
        } catch (Exception e) {
            throw e;
        }
    }

    private static BlankAppearance getBlankAppearance(byte[] imageBytes, String signText, Color color, String signLocationField) throws ParseException {
        BlankAppearance appearance = new BlankAppearance();
        appearance.setReason(REASON);
        appearance.setLocation(LOCATION);
        if (null != imageBytes) {
            appearance.setStamperSr(imageBytes);
            appearance.setGSBlendMode(GSBlendMode.MULTIPLY);
        } else {
            appearance.setSignText(signText);
            appearance.setFontColor(color);
            appearance.setBold(false);
        }
        appearance.setSignFieldName(signLocationField);
        appearance.setTransparency(TRANSPARENCY);
        return appearance;
    }

    private static SignInfoConfig getSignInfoConfigAsyn(X509Cert x509Cert) throws PKIException, SealException {
        SignInfoConfig signInfoConfig = new SignInfoConfig();
        signInfoConfig.setSession(session);
        signInfoConfig.setChain(new X509Cert[] { x509Cert });
        if (x509Cert.isSM2Cert()) {
            signInfoConfig.setSignAlg(Mechanisms.M_SM3_SM2);
            signInfoConfig.setSignatureVersion(SignatureVersion.VERSION_0);
        } else {
            signInfoConfig.setSignAlg(Mechanisms.M_SHA256_RSA);
        }
        signInfoConfig.setAsyn(true);
        return signInfoConfig;
    }
}
