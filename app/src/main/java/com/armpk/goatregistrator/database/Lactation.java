package com.armpk.goatregistrator.database;


import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "Lactation")
public class Lactation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2050724348696580453L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField
    private Integer lactationNumber;

    @DatabaseField
    private Date startDate;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Goat goat;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<MilkControl> lst_milkControls;

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

    public Integer getLactationNumber() {
        return lactationNumber;
    }

    public void setLactationNumber(Integer lactationNumber) {
        this.lactationNumber = lactationNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Goat getGoat() {
        return goat;
    }

    public void setGoat(Goat goat) {
        this.goat = goat;
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

    public ForeignCollection<MilkControl> getLst_milkControls() {
        return lst_milkControls;
    }

    public void setLst_milkControls(ForeignCollection<MilkControl> lst_milkControls) {
        this.lst_milkControls = lst_milkControls;
    }


}
