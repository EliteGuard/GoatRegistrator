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

@DatabaseTable(tableName = "Farm")
public class Farm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2922165046374284517L;

    @SerializedName("id")
	@DatabaseField(id = true)
	private Long _id;
	
	@DatabaseField
	private String companyName;
	
	@DatabaseField
	private String mol;
	
	@DatabaseField
	private Long eik;
	
	@DatabaseField
	private String bulstat;
	
	@DatabaseField
	private String herdbookVolumeNumber;

	@ForeignCollectionField(eager = true)
	private ForeignCollection<Phone> lst_contactPhones;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private User farmer;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Address managementAddress;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Address corespondenceAddress;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Address breedingPlaceAddress;
	
	@DatabaseField
	private String breedingPlaceNumber;

	/*@ForeignCollectionField(eager = true)
	private ForeignCollection<Goat> lst_goats;

    @ForeignCollectionField(eager = true)
	private ForeignCollection<Herd> lst_herds;*/

	@ForeignCollectionField(eager = true)
	private ForeignCollection<VisitProtocol> lst_visitProtocol;

	@DatabaseField
	private Date dateAddedToSystem;
	
	@DatabaseField
	private Date dateLastUpdated;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private User lastUpdatedByUser;
	
	public Farm() { }

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMol() {
		return mol;
	}

	public void setMol(String mol) {
		this.mol = mol;
	}

	public Long getEik() {
		return eik;
	}

	public void setEik(Long eik) {
		this.eik = eik;
	}

	public String getHerdbookVolumeNumber() {
		return herdbookVolumeNumber;
	}

	public void setHerdbookVolumeNumber(String herdbookVolumeNumber) {
		this.herdbookVolumeNumber = herdbookVolumeNumber;
	}

	public ForeignCollection<Phone> getLst_contactPhones() {
		return lst_contactPhones;
	}

	public void setLst_contactPhones(ForeignCollection<Phone> lst_contactPhones) {
		this.lst_contactPhones = lst_contactPhones;
	}

	public User getFarmer() {
		return farmer;
	}

	public void setFarmer(User farmer) {
		this.farmer = farmer;
	}
	
	public Address getManagementAddress() {
		return managementAddress;
	}
	
	public void setManagementAddress(Address managementAddress) {
		this.managementAddress = managementAddress;
	}

	public Address getCorespondenceAddress() {
		return corespondenceAddress;
	}

	public void setCorespondenceAddress(Address corespondenceAddress) {
		this.corespondenceAddress = corespondenceAddress;
	}

	public Address getBreedingPlaceAddress() {
		return breedingPlaceAddress;
	}

	public void setBreedingPlaceAddress(Address breedingPlaceAddress) {
		this.breedingPlaceAddress = breedingPlaceAddress;
	}

	public String getBreedingPlaceNumber() {
		return breedingPlaceNumber;
	}

	public void setBreedingPlaceNumber(String breedingPlaceNumber) {
		this.breedingPlaceNumber = breedingPlaceNumber;
	}

    public ForeignCollection<VisitProtocol> getLst_visitProtocol() {
        return lst_visitProtocol;
    }

    public void setLst_visitProtocol(ForeignCollection<VisitProtocol> lst_visitProtocol) {
        this.lst_visitProtocol = lst_visitProtocol;
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

	public String getBulstat() {
		return bulstat;
	}

	public void setBulstat(String bulstat) {
		this.bulstat = bulstat;
	}

	/*public ForeignCollection<Herd> getLst_herds() {
		return lst_herds;
	}

	public void setLst_herds(ForeignCollection<Herd> lst_herds) {
		this.lst_herds = lst_herds;
	}

	public ForeignCollection<Goat> getLst_goats() {
		return lst_goats;
	}

	public void setLst_goats(ForeignCollection<Goat> lst_goats) {
		this.lst_goats = lst_goats;
	}*/
	
}
