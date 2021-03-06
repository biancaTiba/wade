package models;

import java.util.Date;
import java.util.List;

public class User {

	public String id;
	public String name;
	public String imageUrl;
	public String gender;
	public Date birthdate;
	public List<String> animals;
	public List<String> diseases;
	public List<String> vices;
	public String nationality;
	public List<Child> childs;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public List<String> getAnimals() {
		return animals;
	}

	public void setAnimals(List<String> animals) {
		this.animals = animals;
	}

	public List<String> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<String> diseases) {
		this.diseases = diseases;
	}

	public List<String> getVices() {
		return vices;
	}

	public void setVices(List<String> vices) {
		this.vices = vices;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public List<Child> getChilds() {
		return childs;
	}

	public void setChilds(List<Child> childs) {
		this.childs = childs;
	}

}
