package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "User")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3433337861215907703L;

	@SerializedName("id")
	@DatabaseField(id = true)
	private Long _id;
	
	@DatabaseField
	private String username;
	
	@DatabaseField
	private String password;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private UserRole userRole;
	
	@DatabaseField
	private Boolean activeStatus;
	
	@DatabaseField
	private String firstName;
	
	@DatabaseField
	private String surName;
	
	@DatabaseField
	private String familyName;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private City city;
	
	@DatabaseField
	private String email;
	
	@DatabaseField
	private String phone;
	
	@DatabaseField
	private Date dateRegistered;
	
	@DatabaseField
	private Date dateLastUpdated;

	/*@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private User lastUpdatedByUser;*/
	
	public User() {	}

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public Boolean getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

    /*public User getLastUpdatedByUser() {
        return lastUpdatedByUser;
    }

    public void setLastUpdatedByUser(User lastUpdatedByUser) {
        this.lastUpdatedByUser = lastUpdatedByUser;
    }*/
}
