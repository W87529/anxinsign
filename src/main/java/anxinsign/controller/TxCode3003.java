package anxinsign.controller;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.ContactWayVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3003ReqVO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;

@Controller
public class TxCode3003 {
	@ResponseBody
	@PostMapping(value = "/3003")
    public String txCode3003(String userId,String email,String mobilePhone) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3003ReqVO tx3003ReqVO = new Tx3003ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        tx3003ReqVO.setHead(head);
        ContactWayVO modifyContactWay = new ContactWayVO();
        modifyContactWay.setUserId(userId);
        modifyContactWay.setMobilePhone(mobilePhone);
        if(StringUtils.isNotEmpty(email))
        	modifyContactWay.setEmail(email);
        tx3003ReqVO.setModifyContactWay(modifyContactWay);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3003ReqVO);
        System.out.println("3003-req:" + req);

        String txCode = "3003";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3003-res:" + res);
		return res;
    }
}
