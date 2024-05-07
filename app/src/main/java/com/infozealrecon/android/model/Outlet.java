package com.infozealrecon.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Outlet {
    @SerializedName("PartyMasterList")
    @Expose
    private List<PartyMaster> partyMasterList = null;
    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;

    public List<PartyMaster> getPartyMasterList() {
        return partyMasterList;
    }

    public void setPartyMasterList(List<PartyMaster> partyMasterList) {
        this.partyMasterList = partyMasterList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
