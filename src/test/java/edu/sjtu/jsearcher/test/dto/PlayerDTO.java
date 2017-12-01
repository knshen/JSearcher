package edu.sjtu.jsearcher.test.dto;

public class PlayerDTO {
	String team;
	String name;
	String number;
	String position;
	double height; // m
	double weight; // g
	String birthday;
	int careerAge;
	
	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getCareerAge() {
		return careerAge;
	}

	public void setCareerAge(int careerAge) {
		this.careerAge = careerAge;
	}

	public PlayerDTO() {
		
	}
	
}
