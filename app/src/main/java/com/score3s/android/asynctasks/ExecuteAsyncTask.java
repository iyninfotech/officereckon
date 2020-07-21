package com.score3s.android.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import com.score3s.android.R;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;


// common class for async task, perform API call in  background
public class ExecuteAsyncTask extends AsyncTask<String, Void, String> {

	// Variables
	private Context mContext;
	private onAsyncTaskCompleteListener callback;
	private JSONObject requestedJsonObject;
	private List<NameValuePair> requestedParams;
	private String whatParams;
	private String method = "";
	private String what = ""; 
	private String TAG = "";
	private boolean isProgressBarShown;

	// Construcor
	public ExecuteAsyncTask(Context ctx, JSONObject jObj, String apiMethod, String idOfTask, String tag, String whatparams, onAsyncTaskCompleteListener mAsyncTaskCompleteListener, boolean isProgressBarShow) {
		this.mContext = ctx;
		this.whatParams = whatparams;
		this.requestedJsonObject = jObj;
		this.method = apiMethod;
		this.what = idOfTask;
		this.TAG = tag;
		this.callback = mAsyncTaskCompleteListener;
		isProgressBarShown = isProgressBarShow;
	}
	
	public ExecuteAsyncTask(Context ctx, List<NameValuePair> params, String apiMethod, String idOfTask, String tag, String whatparams, onAsyncTaskCompleteListener mAsyncTaskCompleteListener, boolean isProgressBarShow) {
		this.mContext = ctx;
		this.whatParams = whatparams;
		this.requestedParams = params;
		this.method = apiMethod;
		this.what = idOfTask;
		this.TAG = tag;
		this.callback = mAsyncTaskCompleteListener;
		isProgressBarShown = isProgressBarShow;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(isProgressBarShown){
			CustomProgressbar.showProgressBar(mContext, false, mContext.getString(R.string.please_wait));
		}
		// progress dialog during async task 
	}

	@Override
	protected String doInBackground(String... urls) {
		// get reposnse from server as per API url & it's parameters
		if(whatParams.equals(PARAMETERS.NETWORKS_PARAMS.KEY_REQUESTED_JSON_OBJECT)){
			return WebAPIRequest.getJsonDataStringFormat(urls[0], this.method, requestedJsonObject.toString(), this.TAG);
		}else if(whatParams.equals(PARAMETERS.NETWORKS_PARAMS.KEY_REQUESTED_NAME_VALUE_PAIR)){
			return WebAPIRequest.getJsonDataStringFormat(urls[0], this.method, requestedParams, this.TAG);
		}else{
			return "";
		}

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		

		// notify interface to receive response form API where implemented 
		callback.onAsyncTaskCompleted(result+ PARAMETERS.NETWORKS_PARAMS.KEY_RESPONSE_SEPARATOR+this.what);
		
		// after completion dismiss progress dialog
		if(isProgressBarShown){
			
			new CountDownTimer(1500, 1500) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					
				}
				
				@Override
				public void onFinish() {
					CustomProgressbar.hideProgressBar();
					
				}
			}.start();
		}
	}
}
