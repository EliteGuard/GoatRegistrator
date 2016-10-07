package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "VisitActivity")
public class VisitActivity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8802275653550081694L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Integer _id;

    @DatabaseField
    private String codeName;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private Date dateAddedToSystem;

    @DatabaseField
    private Date dateLastUpdated;

    @DatabaseField (foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private User lastUpdatedByUser;

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        this._id = id;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
