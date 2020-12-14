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
import cfca.trustsign.common.vo.cs.PersonVO;
import cfca.trustsign.common.vo.request.tx3.Tx3001ReqVO;

@Controller
public class TxCode3001 {
	@ResponseBody
	@PostMapping(value = "/3001")
	public String txCode3001(String personName, String identTypeCode, String identNo, String email, String mobilePhone, Integer usedEmailLogin, Integer usedMobileLogin, String address, String authenticationMode, Integer notSendPwd) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3001ReqVO tx3001ReqVO = new Tx3001ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        PersonVO person = new PersonVO();
        person.setPersonName(personName);
        person.setIdentTypeCode(identTypeCode);
        person.setIdentNo(identNo);
        person.setEmail(email);
        person.setMobilePhone(mobilePhone);
        person.setUsedEmailLogin(usedEmailLogin);
        person.setUsedMobileLogin(usedMobileLogin);
        person.setAddress(address);
        person.setAuthenticationMode(authenticationMode);

        tx3001ReqVO.setHead(head);
        tx3001ReqVO.setPerson(person);
        tx3001ReqVO.setNotSendPwd(notSendPwd);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3001ReqVO);
        System.out.println("3001-req:" + req);

        String txCode = "3001";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3001-res:" + res);
        return res;
    }
}
