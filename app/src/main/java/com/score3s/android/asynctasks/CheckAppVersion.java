package com.score3s.android.asynctasks;

//import android.app.AlertDialog;

import android.app.Activity;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Constant.APIURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CheckAppVersion {
	public static Activity CurrentActivity;
	public static String AppVersion;
	public static int matchflag = 0;

	public static final int CheckAppVersion(Activity mCtx , String Authk) {

		CurrentActivity = mCtx;
		final String AuthKey = Authk;
		AppVersion = getAppVersion.getVersionInfo(CurrentActivity);
				AQuery aq;
				aq = new AQuery(CurrentActivity);
				String url = APIURL.BASE_URL + APIURL.LOGIN_AUTH;
				Map<String, String> params = new HashMap<String, String>();
				params.put("AuthKey",AuthKey);
				params.put("AppVersion", AppVersion);

				aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

					@Override
					public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

						CustomProgressbar.hideProgressBar();
						if (jRootObject != null) {
							Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
							try {
								String ErrorMessage = "";
								ErrorMessage = jRootObject.getString("ErrorMessage");
								if (ErrorMessage.equalsIgnoreCase("")) {
									if(jRootObject.getString("LoginStatus").equals("Success")){

										if(jRootObject.getString("ServerAppVersion").equals(AppVersion))
										{
											matchflag = 0;

										}
									}
								} else {
									if (ErrorMessage.equalsIgnoreCase("Invalid App Version"))
									{
										matchflag = 1;
									}
									else if(ErrorMessage.equalsIgnoreCase("Invalid App Authentication"))
									{
										matchflag = 2;

									}
									else
									{
										AlertDialog.Builder builder = new AlertDialog.Builder(CurrentActivity);
										builder.setCancelable(false);
										builder.setTitle("Alert..");
										builder.setMessage(String.format("Please, contact \n 'Infozeal eSolutions Private Limited'"));
										builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {

												CurrentActivity.finish();
												matchflag = 2;
											}
										});
										AlertDialog alert = builder.create();
										alert.show();
									}

								}
							} catch (JSONException e) {
								Log.d("DEBUG", "Json Exception" + e.getMessage());
								e.printStackTrace();

							} catch (Exception e) {
								Log.d("DEBUG", "Exception" + e.getMessage());
								e.printStackTrace();

							}
						} else {

						}
						super.callback(url, jRootObject, status);
					}
				});

		return matchflag;

	}
}
