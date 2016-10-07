package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "Herd")
public class Herd implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3913936490493484751L;

	@SerializedName("id")
	@DatabaseField(id = true)
	private Long _id;
	
	@DatabaseField
	private Integer herdNumber;
	
	/*@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Farm farm;*/

	@ForeignCollectionField(eager = true)
	private ForeignCollection<Goat> lst_goats;
	
	@DatabaseField
	private String description;

	@DatabaseField
	private Double latitude;

	@DatabaseField
	private Double longitude;
	
	@DatabaseField
	private Date dateAddedToSystem;
	
	@DatabaseField
	private Date dateLastUpdated;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
	private User lastUpdatedByUser;
	
	public Herd() { }

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public Integer getHerdNumber() {
		return herdNumber;
	}

	public void setHerdNumber(Integer herdNumber) {
		this.herdNumber = herdNumber;
	}

	/*public Farm getFarm() {
		return farm;
	}

	public void setFarm(Farm farm) {
		this.farm = farm;
	}*/

	public ForeignCollection<Goat> getLst_goats() {
		return lst_goats;
	}

	public void setLst_goats(ForeignCollection<Goat> lst_goats) {
		this.lst_goats = lst_goats;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
