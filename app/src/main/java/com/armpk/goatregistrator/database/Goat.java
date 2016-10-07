package com.armpk.goatregistrator.database;

import com.armpk.goatregistrator.database.enums.GoatKidPurpose;
import com.armpk.goatregistrator.database.enums.Maturity;
import com.armpk.goatregistrator.database.enums.ModifiedBy;
import com.armpk.goatregistrator.database.enums.Sex;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


import java.io.Serializable;
import java.util.Date;
import java.util.List;


@DatabaseTable(tableName = "Goat")
public class Goat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7507334647804492361L;

	@SerializedName("id")
	@DatabaseField(id = true)
	private Long _id;
	
	@DatabaseField
	private Sex sex;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Breed breed;
	
	@DatabaseField
	private Maturity maturity;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private GoatStatus status;
	
	/*@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Farm farm;*/
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Herd herd;
	
	@DatabaseField
	private String birthPlace;
	
	@DatabaseField
	private Date birthDate;
	
	@DatabaseField
	private String color;
	
	@DatabaseField
	private String notes;
	
	@DatabaseField
	private Long herbookEntryNumber;

	@DatabaseField
	private String herbookSection;
	
	@DatabaseField
	private String herbookVolumeNumber;
	
	@DatabaseField(index = true)
	private String firstVeterinaryNumber;
	
	@DatabaseField(index = true)
	private String secondVeterinaryNumber;
	
	@DatabaseField(index = true)
	private String thirdVeterinaryNumber;
	
	@DatabaseField(index = true)
	private String firstBreedingNumber;
	
	@DatabaseField(index = true)
	private String secondBreedingNumber;
	
	@DatabaseField(index = true)
	private String thirdBreedingNumber;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Goat mother;
	
	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Goat father;
	
	@DatabaseField
	private String motherNumber;
	
	@DatabaseField
	private String fatherNumber;

    @DatabaseField
    private Date dateAddedToSystem;

    @DatabaseField
    private Date dateLastUpdated;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private User lastUpdatedByUser;

	@DatabaseField
	private ModifiedBy lastModifiedBy;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<GoatMeasurement> lst_goatMeasurements;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<GoatExteriorMark> lst_goatExteriorMarks;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<Lactation> lst_lactations;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<MilkControl> lst_milkControls;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<FertilityState> lst_fertilityStates;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private FertilityState kidFromPregnancy;

    @DatabaseField
    private GoatKidPurpose goatKidPurpose;
	
	@DatabaseField
	private Double weightAtBirth;
	
	@DatabaseField
	private Double weightAtWeaning;
	
	@DatabaseField
	private Date dateWeaning;

    @DatabaseField
    private Integer numberInCertificate;

    @DatabaseField
    private String certificateNumber;

    // poreden nomer v protokola za brak
    @DatabaseField
    private Integer numberInWastageProtocol;

    @DatabaseField
    private String wastageProtocolNumber;

    // data na otpisvane
    @DatabaseField
    private Date dateDeregistered;

    // nai-poslednata godina za koqto e PODADENA ZA SELEKCIONEN KONTROL
    @DatabaseField
    private Integer appliedForSelectionControlYear;

    // nai-poslednata godina za koqto se vodi kozata, STATUSA /ok ; ne se vodi/
    @DatabaseField
    private Integer inclusionStatusYear;

    // nai-poslednata godina za koqto se vodi kozata, STATUSA /ok ; ne se vodi/
    @DatabaseField
    private String inclusionStatusString;
	
	public Goat() { }

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Breed getBreed() {
		return breed;
	}

	public void setBreed(Breed breed) {
		this.breed = breed;
	}

	public Maturity getMaturity() {
		return maturity;
	}

	public void setMaturity(Maturity maturity) {
		this.maturity = maturity;
	}

	public GoatStatus getStatus() {
		return status;
	}

	public void setStatus(GoatStatus status) {
		this.status = status;
	}

	/*public Farm getFarm() {
		return farm;
	}

	public void setFarm(Farm farm) {
		this.farm = farm;
	}*/

	public Herd getHerd() {
		return herd;
	}

	public void setHerd(Herd herd) {
		this.herd = herd;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

    public Long getHerbookEntryNumber() {
        return herbookEntryNumber;
    }

    public void setHerbookEntryNumber(Long herbookEntryNumber) {
        this.herbookEntryNumber = herbookEntryNumber;
    }

    public String getHerbookSection() {
        return herbookSection;
    }

    public void setHerbookSection(String herbookSection) {
        this.herbookSection = herbookSection;
    }

    public String getHerdbookVolumeNumber() {
        return herbookVolumeNumber;
    }

    public void setHerdbookVolumeNumber(String herdbookVolumeNumber) {
        this.herbookVolumeNumber = herdbookVolumeNumber;
    }

	public String getFirstVeterinaryNumber() {
		return firstVeterinaryNumber;
	}

	public void setFirstVeterinaryNumber(String firstVeterinaryNumber) {
		this.firstVeterinaryNumber = firstVeterinaryNumber;
	}

	public String getSecondVeterinaryNumber() {
		return secondVeterinaryNumber;
	}

	public void setSecondVeterinaryNumber(String secondVeterinaryNumber) {
		this.secondVeterinaryNumber = secondVeterinaryNumber;
	}

	public String getThirdVeterinaryNumber() {
		return thirdVeterinaryNumber;
	}

	public void setThirdVeterinaryNumber(String thirdVeterinaryNumber) {
		this.thirdVeterinaryNumber = thirdVeterinaryNumber;
	}

	public String getFirstBreedingNumber() {
		return firstBreedingNumber;
	}

	public void setFirstBreedingNumber(String firstBreedingNumber) {
		this.firstBreedingNumber = firstBreedingNumber;
	}

	public String getSecondBreedingNumber() {
		return secondBreedingNumber;
	}

	public void setSecondBreedingNumber(String secondBreedingNumber) {
		this.secondBreedingNumber = secondBreedingNumber;
	}

	public String getThirdBreedingNumber() {
		return thirdBreedingNumber;
	}

	public void setThirdBreedingNumber(String thirdBreedingNumber) {
		this.thirdBreedingNumber = thirdBreedingNumber;
	}

	public Goat getMother() {
		return mother;
	}

	public void setMother(Goat mother) {
		this.mother = mother;
	}

	public Goat getFather() {
		return father;
	}

	public void setFather(Goat father) {
		this.father = father;
	}

	public String getMotherNumber() {
		return motherNumber;
	}

	public void setMotherNumber(String motherNumber) {
		this.motherNumber = motherNumber;
	}

	public String getFatherNumber() {
		return fatherNumber;
	}

	public void setFatherNumber(String fatherNumber) {
		this.fatherNumber = fatherNumber;
	}

	public Double getWeightAtBirth() {
		return weightAtBirth;
	}

	public void setWeightAtBirth(Double weightAtBirth) {
		this.weightAtBirth = weightAtBirth;
	}

	public Double getWeightAtWeaning() {
		return weightAtWeaning;
	}

	public void setWeightAtWeaning(Double weightAtWeaning) {
		this.weightAtWeaning = weightAtWeaning;
	}

	public Date getDateWeaning() {
		return dateWeaning;
	}

	public void setDateWeaning(Date dateWeaning) {
		this.dateWeaning = dateWeaning;
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

    public ForeignCollection<GoatMeasurement> getLst_goatMeasurements() {
        return lst_goatMeasurements;
    }

    public void setLst_goatMeasurements(ForeignCollection<GoatMeasurement> lst_goatMeasurements) {
        this.lst_goatMeasurements = lst_goatMeasurements;
    }

    public ForeignCollection<GoatExteriorMark> getLst_goatExteriorMarks() {
        return lst_goatExteriorMarks;
    }

    public void setLst_goatExteriorMarks(
			ForeignCollection<GoatExteriorMark> lst_goatExteriorMarks) {
        this.lst_goatExteriorMarks = lst_goatExteriorMarks;
    }

    public ForeignCollection<Lactation> getLst_lactations() {
        return lst_lactations;
    }

    public void setLst_lactations(ForeignCollection<Lactation> lst_lactations) {
        this.lst_lactations = lst_lactations;
    }

    public ForeignCollection<MilkControl> getLst_milkControls() {
        return lst_milkControls;
    }

    public void setLst_milkControls(ForeignCollection<MilkControl> lst_milkControls) {
        this.lst_milkControls = lst_milkControls;
    }

    public ForeignCollection<FertilityState> getLst_fertilityStates() {
        return lst_fertilityStates;
    }

    public void setLst_fertilityStates(ForeignCollection<FertilityState> lst_fertilityStates) {
        this.lst_fertilityStates = lst_fertilityStates;
    }

    public FertilityState getKidFromPregnancy() {
        return kidFromPregnancy;
    }

    public void setKidFromPregnancy(FertilityState kidFromPregnancy) {
        this.kidFromPregnancy = kidFromPregnancy;
    }

    public GoatKidPurpose getGoatKidPurpose() {
        return goatKidPurpose;
    }

    public void setGoatKidPurpose(GoatKidPurpose goatKidPurpose) {
        this.goatKidPurpose = goatKidPurpose;
    }

    public Integer getNumberInCertificate() {
        return numberInCertificate;
    }

    public void setNumberInCertificate(Integer numberInCertificate) {
        this.numberInCertificate = numberInCertificate;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public Integer getNumberInWastageProtocol() {
        return numberInWastageProtocol;
    }

    public void setNumberInWastageProtocol(Integer numberInWastageProtocol) {
        this.numberInWastageProtocol = numberInWastageProtocol;
    }

    public String getWastageProtocolNumber() {
        return wastageProtocolNumber;
    }

    public void setWastageProtocolNumber(String wastageProtocolNumber) {
        this.wastageProtocolNumber = wastageProtocolNumber;
    }

    public Date getDateDeregistered() {
        return dateDeregistered;
    }

    public void setDateDeregistered(Date dateDeregistered) {
        this.dateDeregistered = dateDeregistered;
    }

    public Integer getAppliedForSelectionControlYear() {
        return appliedForSelectionControlYear;
    }

    public void setAppliedForSelectionControlYear(
            Integer appliedForSelectionControlYear) {
        this.appliedForSelectionControlYear = appliedForSelectionControlYear;
    }

    public Integer getInclusionStatusYear() {
        return inclusionStatusYear;
    }

    public void setInclusionStatusYear(Integer inclusionStatusYear) {
        this.inclusionStatusYear = inclusionStatusYear;
    }

    public ModifiedBy getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(ModifiedBy lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getInclusionStatusString() {
        return inclusionStatusString;
    }

    public void setInclusionStatusString(String inclusionStatusString) {
        this.inclusionStatusString = inclusionStatusString;
    }

}
