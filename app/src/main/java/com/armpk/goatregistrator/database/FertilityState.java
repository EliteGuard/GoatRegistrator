package com.armpk.goatregistrator.database;

import com.armpk.goatregistrator.database.enums.ConceptionType;
import com.armpk.goatregistrator.database.enums.FertilityStateType;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "FertilityState")
public class FertilityState implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -627201814960174977L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField
    private FertilityStateType stateType;

    @DatabaseField
    private Date dateOfState;

    @DatabaseField
    private String notes;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Goat goat;

    @DatabaseField
    private Integer generationNumber;

    @DatabaseField
    private ConceptionType conceptionType;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Goat matedByBuck;

    @DatabaseField
    private String matedByBuckNumber;

    @DatabaseField
    private Date dateDelivery;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Goat> lst_kids;

    @DatabaseField
    private Date dateAddedToSystem;

    @DatabaseField
    private Date dateLastUpdated;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private User lastUpdatedByUser;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Goat getGoat() {
        return goat;
    }

    public void setGoat(Goat goat) {
        this.goat = goat;
    }

    public ConceptionType getConceptionType() {
        return conceptionType;
    }

    public void setConceptionType(ConceptionType conceptionType) {
        this.conceptionType = conceptionType;
    }

    public Goat getMatedByBuck() {
        return matedByBuck;
    }

    public void setMatedByBuck(Goat matedByBuck) {
        this.matedByBuck = matedByBuck;
    }

    public Date getDateDelivery() {
        return dateDelivery;
    }

    public void setDateDelivery(Date dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public ForeignCollection<Goat> getLst_kids() {
        return lst_kids;
    }

    public void setLst_kids(ForeignCollection<Goat> lst_kids) {
        this.lst_kids = lst_kids;
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

    public FertilityStateType getStateType() {
        return stateType;
    }

    public void setStateType(FertilityStateType stateType) {
        this.stateType = stateType;
    }

    public Date getDateOfState() {
        return dateOfState;
    }

    public void setDateOfState(Date dateOfState) {
        this.dateOfState = dateOfState;
    }

    public Integer getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(Integer generationNumber) {
        this.generationNumber = generationNumber;
    }

    public String getMatedByBuckNumber() {
        return matedByBuckNumber;
    }

    public void setMatedByBuckNumber(String matedByBuckNumber) {
        this.matedByBuckNumber = matedByBuckNumber;
    }

}
