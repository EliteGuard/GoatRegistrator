package com.armpk.goatregistrator.database.mobile;

import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.Measurement;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "LocalGoatMeasurement")
public class LocalGoatMeasurement {

    private static final long serialVersionUID = -331124441624497060L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private transient Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private LocalGoat goat;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Measurement measurement;

    @DatabaseField
    private Integer value;

    public LocalGoatMeasurement() { }

    public LocalGoatMeasurement(LocalGoat goat, Measurement measurement) {
        this.goat = goat;
        this.measurement = measurement;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public LocalGoat getLocalGoat() {
        return goat;
    }

    public void setLocalGoat(LocalGoat goat) {
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
