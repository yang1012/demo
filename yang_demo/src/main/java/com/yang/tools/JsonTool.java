package com.yang.tools;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.google.gson.Gson;

/**
 * @ClassName: JsonTool
 * @package com.yang.tools
 * @Description: 标准的Map,json转换。
 * @author Mr.yang
 * @date 2016年1月17日 下午4:57:43
 * @version V1.0
 */
public class JsonTool {

	/**
	 * @Title: jsonToMap
	 * @Description: 将json格式的字符串转换为map
	 * @param json
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:53:34
	 */
	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @Title: mapToJson
	 * @Description:将标准的map信息转换成json字符串格式
	 * @param params
	 * @return
	 * @author Mr.yang
	 * @date 2016年1月17日 下午4:53:53
	 */
	public static String mapToJson(Map<String, Object> params) {
		Gson gson = new Gson();
		return gson.toJson(params);
	}

	public static void main(String[] args) {
		String json = "{\"method\":\"\",\"params\":[{\"skuString\":\"1_31772\"},{\"skuString2\":\"1_3177222222\"}]}";

		// List list = (List) JsonTool.jsonToMap(json).get("params");
		// <String, String> s = (Map<String, String>) list.get(1);
		System.out.println(JsonTool.mapToJson(JsonTool.jsonToMap(json)));
	}

}
