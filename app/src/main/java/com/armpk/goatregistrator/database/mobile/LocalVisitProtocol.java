package com.armpk.goatregistrator.database.mobile;

import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.User;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "LocalVisitProtocol")
public class LocalVisitProtocol implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8426513707190757227L;

    @SerializedName("id")
    @DatabaseField(generatedId = true)
    private Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Farm farm;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Farm farm2;

    @DatabaseField
    private Date visitDate;

    @DatabaseField
    private String notes;

    @DatabaseField (foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private User employFirst;

    @DatabaseField (foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private User employSecond;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<LocalGoat> lst_localGoat;

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

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public void setFarm2(Farm farm) {
        this.farm2 = farm;
    }

    public Farm getFarm2() {
        return farm2;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public User getEmployFirst() {
        return employFirst;
    }

    public void setEmployFirst(User employFirst) {
        this.employFirst = employFirst;
    }

    public User getEmploySecond() {
        return employSecond;
    }

    public void setEmploySecond(User employSecond) {
        this.employSecond = employSecond;
    }

    public ForeignCollection<LocalGoat> getLst_localGoat() {
        return lst_localGoat;
    }

    public void setLst_localGoat(ForeignCollection<LocalGoat> lst_localGoat) {
        this.lst_localGoat = lst_localGoat;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
