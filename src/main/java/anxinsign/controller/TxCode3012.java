package anxinsign.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;
import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SealUpdateVO;
import cfca.trustsign.common.vo.cs.SealVO;
import cfca.trustsign.common.vo.request.tx3.Tx3012ReqVO;
import cfca.sadk.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class TxCode3012 {
	@ResponseBody
	@PostMapping(value = "/3012")
	public static String txCode3012(String userId,String sealId,String imageData) throws PKIException, FileNotFoundException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3012ReqVO tx3012ReqVO = new Tx3012ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        SealUpdateVO sealUpdate = new SealUpdateVO();
        sealUpdate.setUserId(userId);

        SealVO sealVO = new SealVO();
        sealVO.setSealId(sealId);
        sealVO.setImageData(Base64.toBase64String(Files.readAllBytes(Paths.get(imageData))));
        sealUpdate.setSeal(sealVO);

        tx3012ReqVO.setHead(head);
        tx3012ReqVO.setSealUpdate(sealUpdate);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3012ReqVO);
        System.out.println("3012-req:" + req);

        String txCode = "3012";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3012-res:" + res);
		return res;
    }
}
