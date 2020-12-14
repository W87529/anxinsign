package anxinsign.controller;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3211ReqVO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;

@Controller
public class TxCode3211 {
	@ResponseBody
	@PostMapping(value = "/3211")
    public static String txCode3211(String batchNo) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3211ReqVO tx3211ReqVO = new Tx3211ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        tx3211ReqVO.setHead(head);
        tx3211ReqVO.setBatchNo(batchNo);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3211ReqVO);
        System.out.println("3211-req:" + req);

        String txCode = "3211";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3211-res:" + res);
		return res;
    }
}
