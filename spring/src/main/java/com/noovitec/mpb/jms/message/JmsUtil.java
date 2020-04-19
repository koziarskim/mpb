package com.noovitec.mpb.jms.message;

import java.time.LocalDate;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

@Component
public class JmsUtil {

	public boolean getBoolean(String name, String[] keys, Object[] values) {
		boolean value = false;
		if(keys == null || values == null) {
			return value;
		}
		Object obj = values[ArrayUtils.indexOf(keys, name)];
		value = obj==null?false:((boolean) obj);
		return value;
	}

	public LocalDate getLocalDate(String name, String[] keys, Object[] values) {
		LocalDate value = null;
		if(keys == null || values == null) {
			return value;
		}
		Object obj = values[ArrayUtils.indexOf(keys, name)];
		value = obj==null?null:((LocalDate) obj);
		return value;
	}
	
	public long getLong(String name, String[] keys, Object[] values) {
		long value = 0;
		if(keys == null || values == null) {
			return value;
		}
		Object obj = values[ArrayUtils.indexOf(keys, name)];
		value = obj==null?0:((long) obj);
		return value;
	}
}
