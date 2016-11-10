package com.armpk.goatregistrator.database.mobile;

import com.armpk.goatregistrator.database.Breed;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.FertilityState;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.GoatExteriorMark;
import com.armpk.goatregistrator.database.GoatMeasurement;
import com.armpk.goatregistrator.database.GoatStatus;
import com.armpk.goatregistrator.database.Herd;
import com.armpk.goatregistrator.database.Lactation;
import com.armpk.goatregistrator.database.MilkControl;
import com.armpk.goatregistrator.database.User;
import com.armpk.goatregistrator.database.enums.GoatKidPurpose;
import com.armpk.goatregistrator.database.enums.Maturity;
import com.armpk.goatregistrator.database.enums.ModifiedBy;
import com.armpk.goatregistrator.database.enums.Sex;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;


@DatabaseTable(tableName = "LocalGoat")
public class LocalGoat implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 7507334647804492362L;

	@DatabaseField(generatedId = true)
	private transient Long _id;

	@SerializedName("id")
	@DatabaseField
	private Long real_id;

	@DatabaseField
	private Sex sex;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private Breed breed;

	@DatabaseField
	private Maturity maturity;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private GoatStatus status;

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private transient Farm farm;

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
    private transient ForeignCollection<LocalGoatMeasurement> lst_localGoatMeasurements;

    /*@ForeignCollectionField (eager = true)
    private ForeignCollection<GoatExteriorMark> lst_goatExteriorMarks;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<Lactation> lst_lactations;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<MilkControl> lst_milkControls;

    @ForeignCollectionField (eager = true)
    private ForeignCollection<FertilityState> lst_fertilityStates;*/

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

	@DatabaseField (foreign = true, foreignAutoRefresh = true)
	private transient LocalVisitProtocol localVisitProtocol;

	private boolean isInVetIS = false;

	public LocalGoat() { }

    public LocalGoat(Goat goat){
        //this._id = goat.getId();
        if(goat.getAppliedForSelectionControlYear() != null) this.appliedForSelectionControlYear = goat.getAppliedForSelectionControlYear();
        if(goat.getBirthDate() != null) this.birthDate = goat.getBirthDate();
        if(goat.getBirthPlace() != null) this.birthPlace = goat.getBirthPlace();
        if(goat.getBreed() != null) this.breed = goat.getBreed();
        if(goat.getCertificateNumber() != null) this.certificateNumber = goat.getCertificateNumber();
        if(goat.getColor() != null) this.color = goat.getColor();
        if(goat.getDateAddedToSystem() != null) this.dateAddedToSystem = goat.getDateAddedToSystem();
        if(goat.getDateDeregistered() != null) this.dateDeregistered = goat.getDateDeregistered();
        if(goat.getDateLastUpdated() != null) this.dateLastUpdated = goat.getDateLastUpdated();
        if(goat.getDateWeaning() != null) this.dateWeaning = goat.getDateWeaning();
        //this.farm = goat.get;
        if(goat.getFather() != null) this.father = goat.getFather();
        if(goat.getFatherNumber() != null) this.fatherNumber = goat.getFatherNumber();
        if(goat.getFirstBreedingNumber() != null) this.firstBreedingNumber = goat.getFirstBreedingNumber();
        if(goat.getFirstVeterinaryNumber() != null) this.firstVeterinaryNumber = goat.getFirstVeterinaryNumber();
        if(goat.getGoatKidPurpose() != null) this.goatKidPurpose = goat.getGoatKidPurpose();
        if(goat.getHerbookEntryNumber() != null) this.herbookEntryNumber = goat.getHerbookEntryNumber();
        if(goat.getHerbookSection() != null) this.herbookSection = goat.getHerbookSection();
        if(goat.getHerdbookVolumeNumber() != null) this.herbookVolumeNumber = goat.getHerdbookVolumeNumber();
        if(goat.getHerd() != null) this.herd = goat.getHerd();
        if(goat.getInclusionStatusString() != null) this.inclusionStatusString = goat.getInclusionStatusString();
        if(goat.getInclusionStatusYear() != null) this.inclusionStatusYear = goat.getInclusionStatusYear();
        if(goat.getKidFromPregnancy() != null) this.kidFromPregnancy = goat.getKidFromPregnancy();
        if(goat.getLastModifiedBy() != null) this.lastModifiedBy = goat.getLastModifiedBy();
        if(goat.getLastUpdatedByUser() != null) this.lastUpdatedByUser = goat.getLastUpdatedByUser();
        //this.localVisitProtocol = goat.get;
        //this.lst_localGoatMeasurements = goat.get;
        if(goat.getMaturity() != null) this.maturity = goat.getMaturity();
        if(goat.getMother() != null) this.mother = goat.getMother();
        if(goat.getMotherNumber() != null) this.motherNumber = goat.getMotherNumber();
        if(goat.getNotes() != null) this.notes = goat.getNotes();
        if(goat.getNumberInCertificate() != null) this.numberInCertificate = goat.getNumberInCertificate();
        if(goat.getNumberInWastageProtocol() != null) this.numberInWastageProtocol = goat.getNumberInWastageProtocol();
        if(goat.getId() != null) this.real_id = goat.getId();
        if(goat.getSecondBreedingNumber() != null) this.secondBreedingNumber = goat.getSecondBreedingNumber();
        if(goat.getSecondVeterinaryNumber() != null) this.secondVeterinaryNumber = goat.getSecondVeterinaryNumber();
        if(goat.getSex() != null) this.sex = goat.getSex();
        if(goat.getStatus() != null) this.status = goat.getStatus();
        if(goat.getThirdBreedingNumber() != null) this.thirdBreedingNumber = goat.getThirdBreedingNumber();
        if(goat.getThirdVeterinaryNumber() != null) this.thirdVeterinaryNumber = goat.getThirdVeterinaryNumber();
        if(goat.getWastageProtocolNumber() != null) this.wastageProtocolNumber = goat.getWastageProtocolNumber();
        if(goat.getWeightAtBirth() != null) this.weightAtBirth = goat.getWeightAtBirth();
        if(goat.getWeightAtWeaning() != null) this.weightAtWeaning = goat.getWeightAtWeaning();
    }

	public Long getLocalId() {
		return _id;
	}

	public void setLocalId(Long id) {
		this._id = id;
	}

    public Long getRealId() {
        return real_id;
    }

    public void setRealId(Long id) {
        this.real_id = id;
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

	public Farm getFarm() {
		return farm;
	}

	public void setFarm(Farm farm) {
		this.farm = farm;
	}

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

    public ForeignCollection<LocalGoatMeasurement> getLst_goatMeasurements() {
        return lst_localGoatMeasurements;
    }

    public void setLst_localGoatMeasurements(ForeignCollection<LocalGoatMeasurement> lst_localGoatMeasurements) {
        this.lst_localGoatMeasurements = lst_localGoatMeasurements;
    }

    /*public ForeignCollection<GoatExteriorMark> getLst_goatExteriorMarks() {
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
    }*/

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

    public LocalVisitProtocol getLocalVisitProtocol() {
        return localVisitProtocol;
    }

    public void setLocalVisitProtocol(LocalVisitProtocol localVisitProtocol) {
        this.localVisitProtocol = localVisitProtocol;
    }

	public boolean isInVetIS() {
		return isInVetIS;
	}

	public void setInVetIS(boolean inVetIS) {
		isInVetIS = inVetIS;
	}
}
