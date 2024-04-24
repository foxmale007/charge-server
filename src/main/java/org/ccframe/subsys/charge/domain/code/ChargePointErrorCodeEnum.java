package org.ccframe.subsys.charge.domain.code;

import java.util.ArrayList;
import java.util.List;
import org.ccframe.commons.base.ICodeEnum;


public enum ChargePointErrorCodeEnum implements ICodeEnum {
	/**
	 * STATUS 0
	 */
	STATUS_0,

	/**
	 * STATUS 1
	 */
	STATUS_1,

	/**
	 * STATUS 2
	 */
	STATUS_2;

	public static ChargePointErrorCodeEnum fromCode(String code) {
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
		for(ChargePointErrorCodeEnum value: values()){
			result.add(value);
		}
		return result;
	}

}