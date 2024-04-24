package org.ccframe.thirdadapter.sms;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccframe.commons.sms.ISmsAdapter;
import org.ccframe.commons.sms.SmsException;
import org.ccframe.commons.util.JsonBinder;
import org.ccframe.commons.util.Rewriter;
import org.ccframe.subsys.core.queue.SmsQueueData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConfigurationProperties(prefix = "aliyun-sms")
@ConditionalOnProperty(name="app.adapter.sms.adapterBean", havingValue="aliyunSmsAdapter")
public class AliyunSmsAdapter implements ISmsAdapter, InitializingBean{

	private static final String ALIYUN_DYSMS_PRODUCT = "Dysmsapi";
	
	private static final String ALIYUN_DYSMS_DOMAIN = "dysmsapi.aliyuncs.com";
	
	private String accessKeyId;
	
	private String accessKeySecret;
	
	private IAcsClient acsClient;

	private Map<String, String> templateIdMapper;
	
	private Map<String, String> templateContMapper;
	
	private String signName;
	
	public void setSignName(String signName) {
		this.signName = signName;
	}
	
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public void setTemplateIdMapper(Map<String, String> templateIdMapper) {
		this.templateIdMapper = templateIdMapper;
	}

	public void setTemplateContMapper(Map<String, String> templateContMapper) {
		this.templateContMapper = templateContMapper;
	}

	@Override
	public String buildSmsMessage(final SmsQueueData smsParam) {
		String result = new Rewriter("\\$\\{(\\w+)\\}") {
			public String replacement() {
				String descStr = smsParam.getParamMap().get(group(1));
				return descStr == null ? "" : descStr;
			}
		}.rewrite(templateContMapper.get(smsParam.getMsgTemplate()));
		log.info("发送短信给"+smsParam.getReceiverMobile()+":"+result);
		return result;
	}

	@Override
	public String sendSms(SmsQueueData smsParam) throws SmsException {
		SendSmsRequest request = new SendSmsRequest();
		request.setMethod(MethodType.POST);
		request.setPhoneNumbers(smsParam.getReceiverMobile());
		request.setSignName(signName);
		request.setTemplateCode(templateIdMapper.get(smsParam.getMsgTemplate()));
		request.setTemplateParam(JsonBinder.buildNonNullBinder().toJson(smsParam.getParamMap()));
		try {
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
			if(sendSmsResponse.getCode() == null) {
				throw new RuntimeException("Response code is null.");
			}
			switch(sendSmsResponse.getCode()) {
				case "OK":
					return sendSmsResponse.getBizId();
				case "isp.RAM_PERMISSION_DENY":
					throw new SmsException("RAM权限DENY",null);
				case "isv.OUT_OF_SERVICE":
					throw new SmsException("业务停机",null);
				case "isv.PRODUCT_UN_SUBSCRIPT":
					throw new SmsException("未开通云通信产品的阿里云客户",null);
				case "isv.PRODUCT_UNSUBSCRIBE":
					throw new SmsException("产品未开通",null);
				case "isv.ACCOUNT_NOT_EXISTS":
					throw new SmsException("账户不存在",null);
				case "isv.ACCOUNT_ABNORMAL":
					throw new SmsException("账户异常",null);
				case "isv.SMS_TEMPLATE_ILLEGAL":
					throw new SmsException("短信模板不合法",null);
				case "isv.SMS_SIGNATURE_ILLEGAL":
					throw new SmsException("短信签名不合法",null);
				case "isv.INVALID_PARAMETERS":
					throw new SmsException("参数异常",null);
				case "isp.SYSTEM_ERROR":
					throw new SmsException("系统错误",null);
				case "isv.MOBILE_NUMBER_ILLEGAL":
					throw new SmsException("非法手机号",null);
				case "isv.MOBILE_COUNT_OVER_LIMIT":
					throw new SmsException("手机号码数量超过限制",null);
				case "isv.TEMPLATE_MISSING_PARAMETERS":
					throw new SmsException("模板缺少变量",null);
				case "isv.BUSINESS_LIMIT_CONTROL":
					throw new SmsException("业务限流",null);
				case "isv.INVALID_JSON_PARAM":
					throw new SmsException("JSON参数不合法，只接受字符串值",null);
				case "isv.BLACK_KEY_CONTROL_LIMIT":
					throw new SmsException("黑名单管控",null);
				case "isv.PARAM_LENGTH_LIMIT":
					throw new SmsException("参数超出长度限制",null);
				case "isv.PARAM_NOT_SUPPORT_URL":
					throw new SmsException("不支持URL",null);
				case "isv.AMOUNT_NOT_ENOUGH":
					throw new SmsException("账户余额不足",null);
				default:
					throw new SmsException("未知错误代码："+sendSmsResponse.getCode(),null);
			}
		} catch (ClientException e) {
			throw new SmsException(StringUtils.substring("请求失败：" + e.getErrMsg(), 0, 128) , null);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", ALIYUN_DYSMS_PRODUCT, ALIYUN_DYSMS_DOMAIN);
		acsClient = new DefaultAcsClient(profile);
		log.info("短信API类型：阿里云 初始化完毕");
	}

}
