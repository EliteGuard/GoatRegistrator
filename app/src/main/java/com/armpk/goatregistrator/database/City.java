package com.armpk.goatregistrator.database;

import com.armpk.goatregistrator.database.enums.LocationType;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "City")
public class City implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7979673219170931895L;

	@SerializedName("id")
	@DatabaseField(id = true)
	private Long _id;
	
	@DatabaseField
	private String name;

	@DatabaseField
	private LocationType locationType;
	
	@DatabaseField
	private String area;
	
	@DatabaseField
	private String municipality;

	@DatabaseField
	private String regionalCenter;

	@DatabaseField
	private String postcode;

	@DatabaseField
	private Date dateAddedToSystem;

	@DatabaseField
	private Date dateLastUpdated;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User lastUpdatedByUser;

    @DatabaseField
    private Double latitude;

    @DatabaseField
    private Double longitude;
	
	public City() {	}

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}

    public String getRegionalCenter() {
        return regionalCenter;
    }

    public void setRegionalCenter(String regionalCenter) {
        this.regionalCenter = regionalCenter;
    }

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
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

}
