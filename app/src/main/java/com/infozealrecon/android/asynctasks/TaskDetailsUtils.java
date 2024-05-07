package com.infozealrecon.android.asynctasks;

import org.json.JSONException;
import org.json.JSONObject;


public class TaskDetailsUtils {

    public static String getTaskIdFromServerResponse(String res) {
        String id = res.split(PARAMETERS.NETWORKS_PARAMS.KEY_RESPONSE_SEPARATOR)[1];
        return id;
    }

    public static String getTaskResponseFromServerResponse(String res) {
        String originalRes = res.split(PARAMETERS.NETWORKS_PARAMS.KEY_RESPONSE_SEPARATOR)[0];
        return originalRes;
    }


    public static String getResponseStatus(String res) {
        String status = "";
        JSONObject jObj;
        try {
            jObj = new JSONObject(res);
            status = jObj.getString(PARAMETERS.WEB_SERVICE_RESPONSE.KEY_WEB_API_STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;

    }
}
