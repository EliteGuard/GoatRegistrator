package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "VisitProtocol")
public class VisitProtocol implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8426513707190757226L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Farm farm;

    @DatabaseField
    private Date visitDate;

    @DatabaseField
    private String notes;

    @DatabaseField (foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private User employFirst;

    @DatabaseField (foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private User employSecond;

    /*@ForeignCollectionField(eager = true)
    private ForeignCollection<VisitActivity> lst_visitActivity;*/

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

    /*public ForeignCollection<VisitActivity> getLst_visitActivity() {
        return lst_visitActivity;
    }

    public void setLst_visitProtocolVisitActivity(
            ForeignCollection<VisitActivity> lst_visitActivity) {
        this.lst_visitActivity = lst_visitActivity;
    }*/

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
