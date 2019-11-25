package io.github.biezhi.redisdqueue.utils;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

/**
 * GsonUtil
 *
 * @author biezhi
 * @date 2019/11/21
 */
@UtilityClass
public class GsonUtil {

	private static final Gson GSON = new Gson();

	public String toJson(Object value) {
		return GSON.toJson(value);
	}

	public <T> T fromJson(String value, Class<T> type) {
		return GSON.fromJson(value, type);
	}

}
