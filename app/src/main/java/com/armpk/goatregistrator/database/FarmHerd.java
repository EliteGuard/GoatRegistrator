package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "FarmHerd")
public class FarmHerd {

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;
    @DatabaseField(foreign = true)
    private Farm farm;
    @DatabaseField(foreign = true)
    private Herd herd;

    public FarmHerd(){

    }

    public FarmHerd(Farm f, Herd h){
        this.farm = f;
        this.herd = h;
    }

    public Farm getFarm() {
        return farm;
    }

    public Herd getHerd() {
        return herd;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public void setHerd(Herd herd) {
        this.herd = herd;
    }
}
