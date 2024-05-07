package com.infozealrecon.android.remote;

import com.infozealrecon.android.model.PartyMaster;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface OutletService {
    @POST("getpartymaster/")
    Call<List<PartyMaster>> getPartyMasterHead();
}
