package anxinsign.controller;

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
import cfca.trustsign.common.vo.cs.UserInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3006ReqVO;

@Controller
public class TxCode3006 {
	@ResponseBody
	@PostMapping(value = "/3006")
    public String txCode3006(String userId) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3006ReqVO tx3006ReqVO = new Tx3006ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        tx3006ReqVO.setHead(head);
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(userId);
        tx3006ReqVO.setQueryUserInfo(userInfoVO);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3006ReqVO);
        System.out.println("req:" + req);

        String txCode = "3006";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
		return res;
    }
}
