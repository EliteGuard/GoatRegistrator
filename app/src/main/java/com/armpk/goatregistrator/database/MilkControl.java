package com.armpk.goatregistrator.database;

import com.armpk.goatregistrator.database.enums.DayPart;
import com.armpk.goatregistrator.database.enums.MilkControlType;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "MilkControl")
public class MilkControl implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7910332326571559585L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField
    private MilkControlType type;

    @DatabaseField
    private Date dateControl;

    @DatabaseField
    private DayPart dayPart;

    @DatabaseField
    private Double quantityMilked;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Goat goat;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Lactation lactation;

	/*
	 * Kontrolior -> vryzka s protokol za poseshtenie
	 */
    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private VisitProtocol visitProtocol;

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

    public MilkControlType getType() {
        return type;
    }

    public void setType(MilkControlType type) {
        this.type = type;
    }

    public Date getDateControl() {
        return dateControl;
    }

    public void setDateControl(Date dateControl) {
        this.dateControl = dateControl;
    }

    public DayPart getDayPart() {
        return dayPart;
    }

    public void setDayPart(DayPart dayPart) {
        this.dayPart = dayPart;
    }

    public Double getQuantityMilked() {
        return quantityMilked;
    }

    public void setQuantityMilked(Double quantityMilked) {
        this.quantityMilked = quantityMilked;
    }

    public Goat getGoat() {
        return goat;
    }

    public void setGoat(Goat goat) {
        this.goat = goat;
    }

    public Lactation getLactation() {
        return lactation;
    }

    public void setLactation(Lactation lactation) {
        this.lactation = lactation;
    }

    public VisitProtocol getVisitProtocol() {
        return visitProtocol;
    }

    public void setVisitProtocol(VisitProtocol visitProtocol) {
        this.visitProtocol = visitProtocol;
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
