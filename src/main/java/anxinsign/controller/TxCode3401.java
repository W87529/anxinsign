package anxinsign.controller;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.CommonUtil;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;
import cfca.com.itextpdf.text.pdf.PdfReader;
import cfca.sadk.system.FileHelper;
import cfca.sadk.util.Base64;
import cfca.sadk.util.Signature;
import cfca.seal.sadk.DonePdfSeal;
import cfca.seal.sadk.PrePdfReader;
import cfca.seal.sadk.PrePdfSeal;
import cfca.seal.sadk.PrePdfSeal.EncryptionAlgorithm;
import cfca.seal.sadk.PrePdfSealExtra;
import cfca.seal.sadk.security.deferred.ReservedPdfPKCS7;
import cfca.seal.sadk.security.deferred.ReservedPdfPKCS7.Type;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.LocalSignVO;
import cfca.trustsign.common.vo.request.tx3.Tx3401ReqVO;
import cfca.trustsign.common.vo.response.tx3.Tx3401ResVO;

@Controller
public class TxCode3401 {
	@ResponseBody
	@PostMapping(value = "/3401")
    public static String txCode3401(String userId,String stubNo,String projectCode,int isCheckProjectCode,String pdfPath,String imagePath,String respdfPath) throws Exception {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3401ReqVO tx3401ReqVO = new Tx3401ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        LocalSignVO localSign = new LocalSignVO();
        localSign.setStubNo(stubNo);
        localSign.setUserId(userId);
        localSign.setProjectCode(projectCode);
        localSign.setIsCheckProjectCode(isCheckProjectCode);
        // localSign.setCheckExistingStubNo(0);

        byte[] pdfBytes = Files.readAllBytes(Paths.get(pdfPath));
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
//         byte[] imageBytes = SealUtil.getRectanglePng("张三", Color.RED, "宋体");//本地生成个人长方形签章
        // byte[] imageBytes = SealUtil.getCirclePng("XX公司", "宋体", "电子签章", "宋体", Color.RED, 20, -20, "", "宋体", 0, 0, "", "宋体", 5, 0, 200, 200, 1);// 本地生成企业圆形签章

        PrePdfSeal prePdfSeal = new PrePdfSeal(null, null, imageBytes, 0.68f, EncryptionAlgorithm.RSA, PrePdfSeal.HashAlgorithm.SHA256,
                Type.EXTERNAL_RSA_PKCS7);
        PrePdfSealExtra prePdfSealExtra = new PrePdfSealExtra("签名", TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));
        PrePdfReader prePdfReader = new PrePdfReader(pdfBytes);

        DonePdfSeal donePdfSeal = new DonePdfSeal();
        donePdfSeal.initPdfSeal(prePdfSeal);
        donePdfSeal.initPdfSealExtra(prePdfSealExtra);
        donePdfSeal.initPdfReader(prePdfReader);
        donePdfSeal.updateLocationInfoByBlankSignature("Signature1", true);

        ReservedPdfPKCS7 reservedPdfPKCS7 = new ReservedPdfPKCS7();
        pdfBytes = donePdfSeal.createReservedPdfSeal(null, "", "", 0, reservedPdfPKCS7);

        String signatureAttr = Base64.toBase64String(reservedPdfPKCS7.hashPdf);
        localSign.setSignatureAttr(signatureAttr);

        tx3401ReqVO.setHead(head);
        tx3401ReqVO.setLocalSign(localSign);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3401ReqVO);
        System.out.println("req:" + req);

        if (CommonUtil.isEmpty(req)) {
            System.out.println("req is null");
            return "req is null";
        }

        String txCode = "3401";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);

        Tx3401ResVO tx3401ResVO = jsonObjectMapper.readValue(res, Tx3401ResVO.class);
        if (tx3401ResVO.getLocalSign() != null) {
            String signatureOfAttr = tx3401ResVO.getLocalSign().getSignatureOfAttr();
            Signature s = SecurityUtil.verifyP7ByHash(reservedPdfPKCS7.hashPdf, Base64.decode(signatureOfAttr));
            System.out.println(s.getDigestAlgorithm());

            pdfBytes = new DonePdfSeal().mergeReservedPdfSeal(new PdfReader(pdfBytes), reservedPdfPKCS7.fieldName, Base64.decode(signatureOfAttr));
            FileHelper.write(respdfPath, pdfBytes);
        }
		return res;
    }
}
