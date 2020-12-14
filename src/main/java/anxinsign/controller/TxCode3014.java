package anxinsign.controller;

import org.apache.commons.lang3.StringUtils;
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
import cfca.trustsign.common.vo.cs.SealQueryVO;
import cfca.trustsign.common.vo.request.tx3.Tx3014ReqVO;

@Controller
public class TxCode3014 {
	@ResponseBody
	@PostMapping(value = "/3014")
	public static String txCode3014(String userId,String sealId) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3014ReqVO tx3014ReqVO = new Tx3014ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        SealQueryVO sealQuery = new SealQueryVO();
        sealQuery.setUserId(userId);
        if(StringUtils.isNotEmpty(sealId)) {
        	sealQuery.setSealId(sealId);
        }

        tx3014ReqVO.setHead(head);
        tx3014ReqVO.setSealQuery(sealQuery);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3014ReqVO);
        System.out.println("3014-req:" + req);

        String txCode = "3014";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3014-res:" + res);
		return res;
    }
}
