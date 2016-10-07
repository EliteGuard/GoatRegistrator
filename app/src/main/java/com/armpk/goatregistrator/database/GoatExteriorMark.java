package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "GoatExteriorMark")
public class GoatExteriorMark implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2041452575583414235L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Goat goat;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private ExteriorMark exteriorMark;

    public GoatExteriorMark() { }

    public GoatExteriorMark(Goat goat, ExteriorMark exteriorMark) {
        this.goat = goat;
        this.exteriorMark = exteriorMark;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public Goat getGoat() {
        return goat;
    }

    public void setGoat(Goat goat) {
        this.goat = goat;
    }

    public ExteriorMark getExteriorMark() {
        return exteriorMark;
    }

    public void setExteriorMark(ExteriorMark exteriorMark) {
        this.exteriorMark = exteriorMark;
    }

}
