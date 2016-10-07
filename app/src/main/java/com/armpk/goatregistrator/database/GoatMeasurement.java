package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "GoatMeasurement")
public class GoatMeasurement {

    private static final long serialVersionUID = -331124441624497059L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Goat goat;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Measurement measurement;

    @DatabaseField
    private Integer value;

    public GoatMeasurement() { }

    public GoatMeasurement(Goat goat, Measurement measurement) {
        this.goat = goat;
        this.measurement = measurement;
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

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
