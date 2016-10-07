package com.armpk.goatregistrator.database;

import com.armpk.goatregistrator.database.enums.Sex;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "GoatFromVetIs")
public class GoatFromVetIs implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2165356597741920516L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField(index = true)
    private String firstVeterinaryNumber;

    @DatabaseField(index = true)
    private String secondVeterinaryNumber;

    @DatabaseField
    private Sex sex;

    @DatabaseField
    private String breed;

    @DatabaseField
    private String ownerName;

    @DatabaseField
    private String ownerNameShort;

    @DatabaseField
    private Date inOezFrom;

    @DatabaseField
    private Date inOezTo;

    @DatabaseField
    private Date birthDate;

    @DatabaseField
    private Date deathDate;

    @DatabaseField
    private String condition;

    @DatabaseField
    private Boolean selectionControl;

    @DatabaseField
    private Date dateAddedToSystem;

    @DatabaseField
    private Date dateLastModified;

    @DatabaseField
    private String lastModifiedBy;

    @DatabaseField
    private String area;

    @DatabaseField
    private String municipality;

    @DatabaseField
    private Long farmID;

    @DatabaseField
    private String companyName;

    @DatabaseField
    private String breedingPlaceNumber;

    @DatabaseField
    private Boolean importedFileByFarm;

    @DatabaseField
    private Boolean importedFileByArea;

    @DatabaseField
    private String rowNumberFarmFile;

    @DatabaseField
    private String rowNumberAreaFile;

    public GoatFromVetIs() {

    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
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

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerNameShort() {
        return ownerNameShort;
    }

    public void setOwnerNameShort(String ownerNameShort) {
        this.ownerNameShort = ownerNameShort;
    }

    public Date getInOezFrom() {
        return inOezFrom;
    }

    public void setInOezFrom(Date inOezFrom) {
        this.inOezFrom = inOezFrom;
    }

    public Date getInOezTo() {
        return inOezTo;
    }

    public void setInOezTo(Date inOezTo) {
        this.inOezTo = inOezTo;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Date getDateAddedToSystem() {
        return dateAddedToSystem;
    }

    public void setDateAddedToSystem(Date dateAddedToSystem) {
        this.dateAddedToSystem = dateAddedToSystem;
    }

    public Date getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Date dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Boolean getSelectionControl() {
        return selectionControl;
    }

    public void setSelectionControl(Boolean selectionControl) {
        this.selectionControl = selectionControl;
    }

    public String getBreedingPlaceNumber() {
        return breedingPlaceNumber;
    }

    public void setBreedingPlaceNumber(String breedingPlaceNumber) {
        this.breedingPlaceNumber = breedingPlaceNumber;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Long getFarmID() {
        return farmID;
    }

    public void setFarmID(Long farmID) {
        this.farmID = farmID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getImportedFileByFarm() {
        return importedFileByFarm;
    }

    public void setImportedFileByFarm(Boolean importedFileByFarm) {
        this.importedFileByFarm = importedFileByFarm;
    }

    public Boolean getImportedFileByArea() {
        return importedFileByArea;
    }

    public void setImportedFileByArea(Boolean importedFileByArea) {
        this.importedFileByArea = importedFileByArea;
    }

    public String getRowNumberFarmFile() {
        return rowNumberFarmFile;
    }

    public void setRowNumberFarmFile(String rowNumberFarmFile) {
        this.rowNumberFarmFile = rowNumberFarmFile;
    }

    public String getRowNumberAreaFile() {
        return rowNumberAreaFile;
    }

    public void setRowNumberAreaFile(String rowNumberAreaFile) {
        this.rowNumberAreaFile = rowNumberAreaFile;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
}
