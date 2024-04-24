package org.ccframe.subsys.charge.domain.code;

import java.util.ArrayList;
import java.util.List;
import org.ccframe.commons.base.ICodeEnum;


public enum ChargeTransactionStatusCodeEnum implements ICodeEnum {
	/**
	 * 已请求
	 */
	REQUESTED,

	/**
	 * 传输中
	 */
	CHARGING,

	/**
	 * 停止中
	 */
	STOPPING,
	
	/**
	 * 已完成
	 */
	FINISHED;

	public static ChargeTransactionStatusCodeEnum fromCode(String code) {
		try {
			return values()[Integer.parseInt(code)];
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toCode() {
		return Integer.toString(this.ordinal());
	}

	@Override
	public List<ICodeEnum> valueList() {
		List<ICodeEnum> result = new ArrayList<ICodeEnum>();
		for(ChargeTransactionStatusCodeEnum value: values()){
			result.add(value);
		}
		return result;
	}

}