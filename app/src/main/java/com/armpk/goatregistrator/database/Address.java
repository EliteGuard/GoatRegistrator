package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "Address")
public class Address implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1841742219416364548L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

	@DatabaseField
	private String addressTitle;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private City city;

	@DatabaseField
	private String region;

	@DatabaseField
	private String street;

	@DatabaseField
	private String postcode;

	@DatabaseField
	private String email;

	@DatabaseField
	private String notes;

	@DatabaseField
	private Date dateAddedToSystem;

	@DatabaseField
	private Date dateLastUpdated;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User lastUpdatedByUser;
	
	public Address() { }

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public String getAddressTitle() {
		return addressTitle;
	}

	public void setAddressTitle(String addressTitle) {
		this.addressTitle = addressTitle;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

    public Date getDateAddedToSystem() {
        return dateAddedToSystem;
    }

    public void setDateAddedToSystem(Date dateAddedToSystem) {
        this.dateAddedToSystem = dateAddedToSystem;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public User getLastUpdatedByUser() {
        return lastUpdatedByUser;
    }

    public void setLastUpdatedByUser(User lastUpdatedByUser) {
        this.lastUpdatedByUser = lastUpdatedByUser;
    }
	
}
