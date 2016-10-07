package com.armpk.goatregistrator.database;

import com.armpk.goatregistrator.database.enums.DefectGroup;
import com.armpk.goatregistrator.database.enums.ExteriorMarkType;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "ExteriorMark")
public class ExteriorMark implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7234504051243537350L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Integer _id;

    @DatabaseField
    private ExteriorMarkType type;

    @DatabaseField
    private DefectGroup defectGroup;

    @DatabaseField
    private String name;

    @DatabaseField
    private String codeName;

    @DatabaseField
    private Date dateAddedToSystem;

    @DatabaseField
    private Date dateLastUpdated;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User lastUpdatedByUser;

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        this._id = id;
    }

    public ExteriorMarkType getType() {
        return type;
    }

    public void setType(ExteriorMarkType type) {
        this.type = type;
    }

    public DefectGroup getDefectGroup() {
        return defectGroup;
    }

    public void setDefectGroup(DefectGroup defectGroup) {
        this.defectGroup = defectGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
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