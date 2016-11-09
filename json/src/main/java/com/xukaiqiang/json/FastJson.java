package com.xukaiqiang.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xukaiqiang.bean.Student;

/**
 * @Description: FastJson的一些测试 包括：1.json和String的相互转换 2.JsonArray和jsonObject
 *               3.常见的一些方法 等 4.页面处理
 * @author xukaiqiang
 * @date 2016年11月9日 上午9:08:54
 * @modifier
 * @modify-date 2016年11月9日 上午9:08:54
 * @version 1.0
 */
public class FastJson {
	static  Student [] array =new Student[100];
	static  List<Student>  stuList=new ArrayList<Student>();
	static {
		for (int i = 0; i < 100; i++) {
			Student student = new Student("xukaiqiang" + i, 25, "男", "北京",
					new Date(), true);
			array[i]=student;
			stuList.add(student);
		}

	}

	public static void main(String[] args) {
		// String转换为JsonObject
		// stringToJsonObj();
		// String转换为JsonArray
		// stringToJsonArray();
		// jsonObject的put方法和jsonarray的add方法使用
		// methodTest();
		// 将json转换为String
		// jsonToString();
		// java对象转换为json
//		beanToJson();
		// json转换为java对象
		 jsonToBean();

	}

	/**
	 * @Description: json转换为字符串
	 * @author xukaiqiang
	 * @date 2016年11月9日 上午9:11:55
	 * @modifier
	 * @modify-date 2016年11月9日 上午9:11:55
	 * @version 1.0
	 */
	public static void jsonToString() {
		String str = "{\"result\":\"success\",\"message\":\"成功！\",\"data\":[{\"name\":\"Tom\",\"age\":\"20\"}]}";
		JSONObject json = JSONObject.parseObject(str);
		System.out.println(json.toJSONString());
		System.out.println(json.toString());
	}

	/**
	 * @Description: 将String转换为json
	 * @author xukaiqiang
	 * @date 2016年11月9日 上午9:13:27
	 * @modifier
	 * @modify-date 2016年11月9日 上午9:13:27
	 * @version 1.0
	 */
	public static void stringToJsonObj() {
		// 1.String直接转换为json
		String str = "{\"result\":\"success\",\"message\":\"成功！\"}";
		JSONObject json = JSONObject.parseObject(str);
		System.out.println(json);
	}

	/**
	 * @Description:string转jsonArray
	 * @author xukaiqiang
	 * @date 2016年11月9日 上午9:25:50
	 * @modifier
	 * @modify-date 2016年11月9日 上午9:25:50
	 * @version 1.0
	 */
	public static void stringToJsonArray() {
		String str = "{\"result\":\"success\",\"message\":\"成功！\",\"data\":[{\"name\":\"Tom\",\"age\":\"20\"}]}";
		JSONObject json;
		json = JSONObject.parseObject(str);
		System.out.println(json);
		JSONArray jsonArray = JSONArray.parseArray(json.getString("data"));
		System.out.println(jsonArray.toJSONString());
	}

	/**
	 * @Description: 方法测试
	 * @author xukaiqiang
	 * @date 2016年11月9日 上午9:52:47
	 * @modifier
	 * @modify-date 2016年11月9日 上午9:52:47
	 * @version 1.0
	 */
	public static void methodTest() {
		JSONObject json = new JSONObject();
		json.put("name", "xukaiqiang");
		json.put("age", 25);
		json.put("sex", "man");
		// System.out.println(json.toJSONString());
		// System.out.println("姓名是："+json.getString("name"));
		// System.out.println("年龄是："+json.getIntValue("age"));
		JSONArray array = new JSONArray();
		array.add(json);
		System.out.println(array.toJSONString());
	}

	/**
	 * @Description: java对象转换为json
	 * @author xukaiqiang
	 * @date 2016年11月9日 下午3:46:54
	 * @modifier
	 * @modify-date 2016年11月9日 下午3:46:54
	 * @version 1.0
	 */
	public static void beanToJson() {
		Student student = new Student("xukaiqiang", 25, "男", "北京", new Date(),
				true);
		System.out.println(JSON.toJSONString(student));
	}

	
	/**
	 * @Description: json字符串转换为java对象
	 * @author xukaiqiang
	 * @date 2016年11月9日 下午4:03:08
	 * @modifier
	 * @modify-date 2016年11月9日 下午4:03:08
	 * @version 1.0
	*/
		
	public static void jsonToBean() {
		String jsonStr=JSON.toJSONString(array);
		List<Student> students = JSON.parseArray(jsonStr,Student.class);
		for (Student student2 : students) {
			System.out.println(student2.getName() + ":" + student2.getAddress());
		}
		
		String  jsonString=JSON.toJSONString(stuList);
		List<Student> students2 = JSON.parseArray(jsonString,Student.class);
		for (Student student2 : students2) {
			System.out.println(student2.getName() + ":" + student2.getBirthDay());
		}
	}
}
