package com.armpk.goatregistrator.database;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "VisitProtocolVisitActivity")
public class VisitProtocolVisitActivity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2924053278961308908L;

    @SerializedName("id")
    @DatabaseField(id = true)
    private Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private VisitProtocol visitProtocol;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private VisitActivity visitActivity;

    public VisitProtocolVisitActivity() {}

    public VisitProtocolVisitActivity(VisitProtocol visitProtocol,
                                      VisitActivity visitActivity) {
        this.visitProtocol = visitProtocol;
        this.visitActivity = visitActivity;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public VisitProtocol getVisitProtocol() {
        return visitProtocol;
    }

    public void setVisitProtocol(VisitProtocol visitProtocol) {
        this.visitProtocol = visitProtocol;
    }

    public VisitActivity getVisitActivity() {
        return visitActivity;
    }

    public void setVisitActivity(VisitActivity visitActivity) {
        this.visitActivity = visitActivity;
    }
}
