package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "FarmGoat")
public class FarmGoat {

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;
    @DatabaseField(foreign = true)
    private Farm farm;
    @DatabaseField(foreign = true)
    private Goat goat;

    public FarmGoat(){

    }

    public FarmGoat(Farm f, Goat g){
        this.farm = f;
        this.goat = g;
    }

    public Farm getFarm() {
        return farm;
    }

    public Goat getGoat() {
        return goat;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public void setGoat(Goat goat) {
        this.goat = goat;
    }
}
