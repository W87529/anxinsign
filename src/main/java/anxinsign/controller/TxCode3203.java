package anxinsign.controller;

import java.io.File;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;
import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.UploadContractVO;
import cfca.trustsign.common.vo.request.tx3.Tx3203ReqVO;

@Controller
public class TxCode3203 {
	@ResponseBody
	@PostMapping(value = "/3203")
    public static String txCode3203(@RequestBody JSONObject json) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3203ReqVO tx3203ReqVO = new Tx3203ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

//        UploadContractVO uploadContract = new UploadContractVO();
        JSONObject upCont = json.getJSONObject("uploadContract");

//        JSONArray sis = upCont.getJSONArray("signInfos");
//        if(sis.size()>0) {
//        	UploadSignInfoVO[] signInfos = new UploadSignInfoVO[sis.size()];
//        	for (int i = 0; i < sis.size(); i++) {
//            	JSONObject si = sis.getJSONObject(i);
//        		UploadSignInfoVO signInfoVO0 = new UploadSignInfoVO();
//        		signInfoVO0.setUserId(si.getString("userId"));
//                signInfoVO0.setLocation(si.getString("location"));
//                JSONObject siskw = si.getJSONObject("signKeyword");
//                if(siskw.size()>0) {
//                	SignKeywordVO signKeyword = new SignKeywordVO();
//                    signKeyword.setKeyword(siskw.getString("keyword"));
//                    signKeyword.setOffsetCoordX(siskw.getString("offsetCoordX"));
//                    signKeyword.setOffsetCoordY(siskw.getString("offsetCoordY"));
//                    signKeyword.setImageWidth(siskw.getString("imageWidth"));
//                    signKeyword.setImageHeight(siskw.getString("imageHeight"));
//                    signInfoVO0.setSignKeyword(signKeyword);
//                }
//                JSONArray signLocations = upCont.getJSONArray("signLocations");
//                if(signLocations.size()>0) {
//                	SignLocationVO[] signLocationsPlat = new SignLocationVO[signLocations.size()];
//                    for (int i1 = 0; i1 < signLocations.size(); i1++) {
//                    	JSONObject signLocation = signLocations.getJSONObject(i1);
//                        SignLocationVO signLocationPlat = new SignLocationVO();
//                        signLocationPlat.setSignOnPage(signLocation.getString("signOnPage"));
//                        signLocationPlat.setSignLocationLBX(signLocation.getString("signLocationLBX"));
//                        signLocationPlat.setSignLocationLBY(signLocation.getString("signLocationLBY"));
//                        signLocationPlat.setSignLocationRUX(signLocation.getString("signLocationRUX"));
//                        signLocationPlat.setSignLocationRUY(signLocation.getString("signLocationRUY"));
//                        signLocationsPlat[i1] = signLocationPlat;
//            		}
//                    signInfoVO0.setSignLocations(signLocationsPlat);
//                }
//                signInfoVO0.setProjectCode(si.getString("projectCode"));
//                signInfoVO0.setIsCheckProjectCode(si.getInteger("isCheckProjectCode"));
//                signInfoVO0.setAuthorizationTime(si.getString("authorizationTime"));
//                signInfoVO0.setIsProxySign(si.getInteger("isProxySign"));
//                signInfoVO0.setIsCopy(si.getInteger("isCopy"));
//                signInfoVO0.setSealId(si.getString("sealId"));
//                signInfoVO0.setSealColor(si.getInteger("sealColor"));
//                signInfos[i] = signInfoVO0;
//        	}
//            uploadContract.setSignInfos(signInfos);
//        }
//        
//        uploadContract.setContractTypeCode(upCont.getString("contractTypeCode"));
//        uploadContract.setContractName(upCont.getString("contractName"));
//        uploadContract.setExpiredDate(upCont.getString("expiredDate"));
//        uploadContract.setIsSign(upCont.getInteger("isSign"));
//        uploadContract.setSealId(upCont.getString("sealId"));
//        uploadContract.setSealColor(upCont.getInteger("sealColor"));
//        
//        JSONObject skw = upCont.getJSONObject("signKeyword");
//        if(skw.size()>0) {
//        	SignKeywordVO signKeyword = new SignKeywordVO();
//            signKeyword.setKeyword(skw.getString("keyword"));
//            signKeyword.setOffsetCoordX(skw.getString("offsetCoordX"));
//            signKeyword.setOffsetCoordY(skw.getString("offsetCoordY"));
//            signKeyword.setImageWidth(skw.getString("imageWidth"));
//            signKeyword.setImageHeight(skw.getString("imageHeight"));
//             uploadContract.setSignKeyword(signKeyword);
//        }
//        JSONArray signLocations = upCont.getJSONArray("signLocations");
//        if(signLocations.size()>0) {
//        	SignLocationVO[] signLocationsPlat = new SignLocationVO[signLocations.size()];
//            for (int i = 0; i < signLocations.size(); i++) {
//            	JSONObject signLocation = signLocations.getJSONObject(i);
//                SignLocationVO signLocationPlat = new SignLocationVO();
//                signLocationPlat.setSignOnPage(signLocation.getString("signOnPage"));
//                signLocationPlat.setSignLocationLBX(signLocation.getString("signLocationLBX"));
//                signLocationPlat.setSignLocationLBY(signLocation.getString("signLocationLBY"));
//                signLocationPlat.setSignLocationRUX(signLocation.getString("signLocationRUX"));
//                signLocationPlat.setSignLocationRUY(signLocation.getString("signLocationRUY"));
//                signLocationsPlat[i] = signLocationPlat;
//    		}
//            uploadContract.setSignLocations(signLocationsPlat);
//        }

        tx3203ReqVO.setHead(head);
//        tx3203ReqVO.setUploadContract(uploadContract);
        tx3203ReqVO.setUploadContract(JSON.parseObject(upCont.toString(), UploadContractVO.class));

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3203ReqVO);
        System.out.println("3203-req:" + req);

        File file = new File(json.getString("contractFile"));

        String txCode = "3203";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature, file);
        System.out.println("3203-res:" + res);
		return res;
    }
}
