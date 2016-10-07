package com.armpk.goatregistrator.database.mobile;

import com.armpk.goatregistrator.database.VisitActivity;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "LocalVisitProtocolVisitActivity")
public class LocalVisitProtocolVisitActivity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2924053278961308909L;

    @SerializedName("id")
    @DatabaseField(generatedId = true)
    private Long _id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private LocalVisitProtocol localVisitProtocol;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private VisitActivity visitActivity;

    public LocalVisitProtocolVisitActivity() {}

    public LocalVisitProtocolVisitActivity(LocalVisitProtocol localVisitProtocol,
                                           VisitActivity visitActivity) {
        this.localVisitProtocol = localVisitProtocol;
        this.visitActivity = visitActivity;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public LocalVisitProtocol getLocalVisitProtocol() {
        return localVisitProtocol;
    }

    public void setLocalVisitProtocol(LocalVisitProtocol localVisitProtocol) {
        this.localVisitProtocol = localVisitProtocol;
    }

    public VisitActivity getVisitActivity() {
        return visitActivity;
    }

    public void setVisitActivity(VisitActivity visitActivity) {
        this.visitActivity = visitActivity;
    }
}
