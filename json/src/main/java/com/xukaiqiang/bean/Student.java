package com.xukaiqiang.bean;

import java.util.Date;

/**
 * @Description: 学生实体类
 * @author xukaiqiang
 * @date 2016年11月9日 下午3:36:53
 * @modifier
 * @modify-date 2016年11月9日 下午3:36:53
 * @version 1.0
 */

public class Student {
	private String name;
	private int age;
	private String sex;
	private String address;
	private Date birthDay;
	private boolean isMarraied;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public boolean isMarraied() {
		return isMarraied;
	}

	public void setMarraied(boolean isMarraied) {
		this.isMarraied = isMarraied;
	}

	public Student(String name, int age, String sex, String address,
			Date birthDay, boolean isMarraied) {
		super();
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.address = address;
		this.birthDay = birthDay;
		this.isMarraied = isMarraied;
	}

	public Student() {
		super();
	}

}
