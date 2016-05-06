package dto.user;

import java.util.*;
import java.util.Map.Entry;

/**
 * This is a user defined DTO class (for inserting a object to DB)
 * A DTO class must implement setter and getter for every field
 * @author ShenKai
 *
 */
public class Person {
	private int age;
	private String sex;
	
	public Person() {
		
	}
	
	public Person(int age, String sex) {
		this.age = age;
		this.sex = sex;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public String getSex() {
		return this.sex;
	}
	
	public String toString() {
		return "[" + this.sex + " " + this.age + "]";
	}
	
}
