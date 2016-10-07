package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "Breed")
public class Breed implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5063066067457428783L;

	@SerializedName("id")
	@DatabaseField(id = true)
	private Integer _id;
	
	@DatabaseField
	private String breedName;
	
	@DatabaseField
	private String codeName;

	@DatabaseField
	private String description;

	@DatabaseField
	private Date dateAddedToSystem;

	@DatabaseField
	private Date dateLastUpdated;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User lastUpdatedByUser;

	public Breed() { }

	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		this._id = id;
	}

	public String getBreedName() {
		return breedName;
	}

	public void setBreedName(String breedName) {
		this.breedName = breedName;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
