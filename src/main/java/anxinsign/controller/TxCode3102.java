package anxinsign.controller;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.ProxySignVO;
import cfca.trustsign.common.vo.request.tx3.Tx3102ReqVO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;

@Controller
public class TxCode3102 {
	@ResponseBody
	@PostMapping(value = "/3102")
    public static String txCode3102(String userId,String projectCode,String checkCode) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3102ReqVO tx3102ReqVO = new Tx3102ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        ProxySignVO proxySignVO = new ProxySignVO();
        proxySignVO.setUserId(userId);
        proxySignVO.setProjectCode(projectCode);
        proxySignVO.setCheckCode(checkCode);

        tx3102ReqVO.setHead(head);
        tx3102ReqVO.setProxySign(proxySignVO);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3102ReqVO);
        System.out.println("3102-req:" + req);

        String txCode = "3102";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3102-res:" + res);
		return res;
    }
}
