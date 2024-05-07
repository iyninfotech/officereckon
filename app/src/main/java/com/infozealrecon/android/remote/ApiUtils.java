package com.infozealrecon.android.remote;

public class ApiUtils {
    private void APIUtils(){
    };

    public static final String API_URL = "http://103.250.144.192:8555/api/v1/";

    public static OutletService getPartyMasterHead(){

        return RetrofitClient.getClient(API_URL).create(OutletService.class);
    }
}
