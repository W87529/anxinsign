package anxinsign.controller;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.EnterpriseTransactorVO;
import cfca.trustsign.common.vo.cs.EnterpriseVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3002ReqVO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;

@Controller
public class TxCode3002 {
	@ResponseBody
	@PostMapping(value = "/3002")
	public String txCode3002(String enterpriseName,String identTypeCode,String identNo,String email,String mobilePhone
			,Integer usedEmailLogin,Integer usedMobileLogin,String landlinePhone,String authenticationMode
			,String transactorName,String identTypeCode2,String identNo2,String address,Integer notSendPwd) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3002ReqVO tx3002ReqVO = new Tx3002ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        EnterpriseVO enterprise = new EnterpriseVO();
        enterprise.setEnterpriseName(enterpriseName);
        enterprise.setIdentTypeCode(identTypeCode);
        enterprise.setIdentNo(identNo);
        enterprise.setEmail(email);
        enterprise.setMobilePhone(mobilePhone);
        enterprise.setUsedEmailLogin(usedEmailLogin);
        enterprise.setUsedMobileLogin(usedMobileLogin);
        enterprise.setLandlinePhone(landlinePhone);
        enterprise.setAuthenticationMode(authenticationMode);

        EnterpriseTransactorVO enterpriseTransactor = new EnterpriseTransactorVO();
        enterpriseTransactor.setTransactorName(transactorName);
        enterpriseTransactor.setIdentTypeCode(identTypeCode2);
        enterpriseTransactor.setIdentNo(identNo2);
        enterpriseTransactor.setAddress(address);

        tx3002ReqVO.setHead(head);
        tx3002ReqVO.setEnterprise(enterprise);
        tx3002ReqVO.setEnterpriseTransactor(enterpriseTransactor);
        tx3002ReqVO.setNotSendPwd(notSendPwd);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3002ReqVO);
        System.out.println("3002-req:" + req);

        String txCode = "3002";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3002-res:" + res);
		return res;
    }
}
