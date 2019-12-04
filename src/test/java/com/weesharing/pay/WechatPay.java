package com.weesharing.pay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.weesharing.pay.exception.ServiceException;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class WechatPay {
	
	private static final String apipay_sit_url = "https://apipay-sit.weesharing.com/fcp/service";
	private static final String bizID = "e_13311039373_20191031110639";
	private static final String bizID2 = "1188650507552423936";
	
	private static final Long platform_code = 1001000L;
	private static final String private_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAN7kEUbEO8IGplsAlV4JBgGvuSliAoaInDyqSCmcOxtkk/Z3wLhRoJb3jCMh3wTczRuXDDnE3BbzpFxR7aSPW5GO+uOou2XmJeiMYQ+mI8Cq3XhHQ5bTkpg0Agq4JO+DhBQPnYytzKLIa40YUijnFtTKEqh8jSqKnvl0LUMsgqN1AgMBAAECgYEArKCrGMoHFmEcGsM67FfExS2aFQkJt7S6fOnNdhzAUpj1WCCrrJXb8NDNQrCLEDMK0GGOYHetlkEhfTce/SLi4Ul9FQAASW2v0NnWnSaPK5+ZXo8UbeoiDhNLYNNhun3L5m1OQnFx6ctQasi/UWPqvl6P25UGjQXTQElVF/1P7wkCQQDvv32IrAq+DXq6glpvNjz+D07+gEaNHw26rCk2ms2OykhhdDIP1fBP/pHcUVttvciPw+mEfvHWh7f0caO0sN7bAkEA7gALSR41BxBlnumoTqk75s0PnD+sEys0lfSMz6GGdLf/vmX1WrYX3qB+3/+U1sN8NTuPQ0EKOvvmLXxpHedP7wJBAN2XolHWoj8zekI1BZ1RBmLUh3DX413AXBIz2gvsXR4jfW8F1NeqJ+noI6z9TYJLLbsjmwypwKFG5BH9jxANgo0CQGbaOgZVOZFd8qykSYoE9NAfWlmp4pE+ILGVR60LHvId+jWsFkYnX1VkgrAG4amWPX07ygPEfclTxvttQngb2J0CQQCyW0Z1QZBniReauAIlX8jYP4Rk4K8rgGzgjhnJDgT+doRGAAl5F8Z/FPJ0PQZRC0MDqbP6SAOCZhR5Dnigl18W";
	private static final String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDe5BFGxDvCBqZbAJVeCQYBr7kpYgKGiJw8qkgpnDsbZJP2d8C4UaCW94wjId8E3M0blww5xNwW86RcUe2kj1uRjvrjqLtl5iXojGEPpiPAqt14R0OW05KYNAIKuCTvg4QUD52MrcyiyGuNGFIo5xbUyhKofI0qip75dC1DLIKjdQIDAQAB";
	private static final String weesharing_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEfI63zUPrqFMxOzzZoW0+IS+pskMf5W/CztOSGTFlJ0yjqR6XMCpYk1T3/FHfmMQIsKYUbfNfKrqWO6WCWZAIp6QaZbFUFF93PYibP8pwNyYNwUmbNmktqyNsHRZsaVRNFBIsjWbsXeHCmePA4AW6bd/+44g5lPR8gLbdKm2iDwIDAQAB";
	@Test
	public void wechatPay() {
		String requestNo = RandomUtil.randomNumbers(10);
		String platformUserNo=RandomUtil.randomNumbers(10);
		String payOrderNo = RandomUtil.randomString(11);
		String subject = "下单" + payOrderNo;
		String body = "买东西" + payOrderNo;
		userEntryInfo(requestNo, platformUserNo);
		onlineOrderPay(requestNo, platformUserNo, payOrderNo, subject, body);
		orderRefundPay(requestNo, platformUserNo, payOrderNo);
		
	}
	
	public String userEntryInfo(String requestNo, String platformUserNo) {
		String resp = HttpUtil.post(apipay_sit_url, userEntryInfoParam(requestNo, platformUserNo));
		log.info("[userEntryInfo] resp:" + resp);
		boolean verify = verifyJson(resp);
		log.info("[userEntryInfo] verify:" + verify);
		if(verify) {
			JSONObject result =JSONUtil.parseObj(resp);
			return result.getStr("respData");
		}else {
			throw new ServiceException("USER_ENTRY_INFO verify exception");
		}
	}
	
	public String onlineOrderPay(String requestNo, String platformUserNo, String payOrderNo, String subject, String body) {
		String resp = HttpUtil.post(apipay_sit_url, onlineOrderPayParam(requestNo, platformUserNo, payOrderNo, subject, body));
		log.info("[onlineOrderPay] resp:" + resp);
		boolean verify = verifyJson(resp);
		log.info("[onlineOrderPay] verify:" + verify);
		if(verify) {
			JSONObject result =JSONUtil.parseObj(resp);
			return result.getStr("respData");
		}else {
			throw new ServiceException("USER_ENTRY_INFO verify exception");
		}
	}
	
	public String orderRefundPay(String requestNo, String platformUserNo, String payOrderNo) {
		String resp = HttpUtil.post(apipay_sit_url, orderRefundParam(requestNo, platformUserNo, payOrderNo));
		log.info("[orderRefundPay] resp:" + resp);
		boolean verify = verifyJson(resp);
		log.info("[orderRefundPay] verify:" + verify);
		if(verify) {
			JSONObject result =JSONUtil.parseObj(resp);
			return result.getStr("respData");
		}else {
			throw new ServiceException("ORDER_REFUND verify exception");
		}
	}
	
	private String onlineOrderPayParam(String requestNo, String platformUserNo, String payOrderNo, String subject, String body) {
		/**
		 * =============================================
		 * 封装ONLINE_ORDER_PAY参数
		 * =============================================
		{
		    serviceName: 'ONLINE_ORDER_PAY',
		    platformNo: '2046',
		    reqData: {
		        requestNo: '200045435'
		        platformUserNo: USER001,
		        platformMchNo: VANKE001,
		        platformOrderNo: OTTR3432432,
		        paymentMethod: 'BALANCE',
		        currency: 'CNY',
		        amount: 123.54,
		        actualAmount: 123.54,
		        subject: '午餐B套餐',
		        body: '园区A-午餐-B套餐',
		        customDefine: ABCDEF,
		        timestamp: 20160102010234,
		    },
		  sign: 'sdfdksajfjddalsjhdghlhadlfhlddhflksdalkfhsdkalf'
		}
		 */
		
		Map<String, Object> param = new HashMap<>();
		param.put("serviceName", "ONLINE_ORDER_PAY");
		param.put("platformNo", platform_code);
		
		Map<String, Object> reqData = new HashMap<>();
		reqData.put("requestNo", requestNo);
		reqData.put("platformUserNo", platformUserNo);
		
		reqData.put("platformMchNo", bizID2);
		reqData.put("platformOrderNo", payOrderNo);
		reqData.put("paymentMethod", "WECHAT");
		reqData.put("currency", "CNY");
		reqData.put("amount", 0.01);
		reqData.put("actualAmount", 0.01);
		reqData.put("subject", subject);
		reqData.put("body", body);
		reqData.put("customDefine", "AGGPAY");
		
		reqData.put("realIp", "192.168.0.1");
		reqData.put("redirectUrl", "http://www.baidu.com");
		
		
		reqData.put("timestamp", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		
		param.put("reqData", JSONUtil.wrap(reqData, false).toString());
		param.put("sign", signJson(JSONUtil.wrap(reqData, false).toString()));
		
		String json = JSONUtil.wrap(param, false).toString();
		log.info("[unifiedOrderPayParam]:" + json);
		return json;
	}
	
	private String userEntryInfoParam(String requestNo, String platformUserNo) {
		/**
		 * =============================================
		 * 封装USER_ENTRY_INFO参数
		 * =============================================
		{
		    serviceName: 'USER_ENTRY_INFO',
		    platformNo: '2046',
		    reqData: {
		        requestNo: '200045435'
		        platformUserNo: USER001,
		        timestamp: 20160102010234
		    },
		  sign: 'sdfdksajfjddalsjhdghlhadlfhlddhflksdalkfhsdkalf'
		}
	   */
		
		Map<String, Object> param = new HashMap<>();
		param.put("serviceName", "USER_ENTRY_INFO");
		param.put("platformNo", platform_code);
		
		Map<String, Object> reqData = new HashMap<>();
		reqData.put("requestNo", requestNo);
		reqData.put("platformUserNo", platformUserNo);
		reqData.put("timestamp", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		
		param.put("reqData", JSONUtil.wrap(reqData, false).toString());
		param.put("sign", signJson(JSONUtil.wrap(reqData, false).toString()));
		
		String json = JSONUtil.wrap(param, false).toString();
		log.info("[userEntryInfoParam]:" + json);
		return json;

	}
	
	private String orderRefundParam(String requestNo, String platformUserNo, String payOrderNo) {
		/**
		 * =============================================
		 * 封装ORDER_REFUND参数
		 * =============================================
		{
			serviceName: 'ORDER_REFUND',
			platformNo: '2046',
			reqData: {
				requestNo: '200045435'
				platformUserNo: USER001,
				platformOrderNo: OTTR3432432,
				customDefine: ABCDEF,
				timestamp: 20160102010234,
			},
		  	sign: 'sdfdksajfjddalsjhdghlhadlfhlddhflksdalkfhsdkalf'
		}
	   */
		
		Map<String, Object> param = new HashMap<>();
		param.put("serviceName", "ORDER_REFUND");
		param.put("platformNo", platform_code);
		
		Map<String, Object> reqData = new HashMap<>();
		reqData.put("requestNo", requestNo);
		reqData.put("platformOrderNo", payOrderNo);
		reqData.put("platformUserNo", platformUserNo);
		reqData.put("customDefine", "AGGPAY");
		reqData.put("timestamp", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		
		param.put("reqData", JSONUtil.wrap(reqData, false).toString());
		param.put("sign", signJson(JSONUtil.wrap(reqData, false).toString()));
		
		String json = JSONUtil.wrap(param, false).toString();
		log.info("[orderRefundParam]:" + json);
		return json;

	}
	
	private String signJson(String json) {
		byte[] data = json.getBytes();
		Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, private_key, weesharing_public_key);
		String signed = Base64.encode(sign.sign(data));
		return signed;
	}
	
	private boolean verifyJson(String json) {
		JSONObject result =JSONUtil.parseObj(json);
		Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, private_key, weesharing_public_key);
		String signStr = result.getStr("sign");
		String respData = result.getStr("respData");
		return sign.verify(respData.getBytes(), Base64.decode(signStr));
	}
}
