package com.score3s.android.asynctasks;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.score3s.android.R;


/**
 * Class to provide custom progress bar throughout application.
 * 
 * @author Innoppl
 * 
 */

public class CustomProgressbar extends Dialog {

	static CustomProgressbar mCustomProgressbar;

	private CustomProgressbar mProgressbar;

	TextView mTextViewMessage;

	String mMessage;

	OnDismissListener mOnDissmissListener;

	/**
	 * Constructor with single parameter
	 * @param context
	 */
	private CustomProgressbar(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_progressbar);
		mTextViewMessage = (TextView) findViewById(R.id.progressbar_textview_message);
		this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}

	/**
	 * Constructor with two parameter
	 * @param context
	 * @param instance
	 */
	public CustomProgressbar(Context context, Boolean instance) {
		super(context);
		mProgressbar = new CustomProgressbar(context);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		if (mOnDissmissListener != null) {
			mOnDissmissListener.onDismiss(this);
		}
	}

	/**
	 * Method to show progress bar without text
	 * @param context
	 * @param cancelable
	 */
	public static void showProgressBar(Context context, boolean cancelable) {
		showProgressBar(context, cancelable, null);
	}
	


	/**
	 * Method to show progress bar with text
	 * @param context
	 * @param cancelable
	 * @param message
	 */
	public static void showProgressBar(Context context, boolean cancelable, String message) {

		if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
			mCustomProgressbar.cancel();
		}
		mCustomProgressbar = new CustomProgressbar(context);
		mCustomProgressbar.setCancelable(cancelable);
		if (StringUtils.isNotBlank(message)) {
			mCustomProgressbar.setMessage(message);
		}
		
		mCustomProgressbar.show();
		
	}

	
	/**
	 * Method to call show progress bar with callback listner
	 * @param context
	 * @param listener
	 */
	public static void showProgressBar(Context context, OnDismissListener listener) {

		if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
			mCustomProgressbar.cancel();
		}

		if (listener == null) {
			Log.i("CustomProgressbar", "You have not set the listener for the progressbar");
		}

		mCustomProgressbar = new CustomProgressbar(context);
		mCustomProgressbar.setListener(listener);
		mCustomProgressbar.setCancelable(Boolean.TRUE);
		mCustomProgressbar.show();
	}

	/**
	 * Method to hide progress bar
	 */
	public static void hideProgressBar() {
		if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
			mCustomProgressbar.dismiss();
		}
	}

	private void setListener(OnDismissListener listener) {
		mOnDissmissListener = listener;

	}

	/**
	 * Method to set message by passing string
	 * 
	 * @param message
	 */
	private void setMessage(String message) {
		mTextViewMessage.setText(message);
	}

	/**
	 * Method to set position to show the progress bar
	 * 
	 * @param view
	 */
	public static void showListViewBottomProgressBar(View view) {
		if (mCustomProgressbar != null) {
			mCustomProgressbar.dismiss();
		}

		view.setVisibility(View.VISIBLE);
	}

	/**
	 * Method to hide the listview bottom progress bar 
	 * 
	 * @param view
	 */
	public static void hideListViewBottomProgressBar(View view) {
		if (mCustomProgressbar != null) {
			mCustomProgressbar.dismiss();
		}

		view.setVisibility(View.GONE);
	}

	/**
	 * Method to show the progress bar
	 * 
	 * @param context
	 * @param cancelable
	 * @param message
	 */
	public void showProgress(Context context, boolean cancelable, String message) {

		if (mProgressbar != null && mProgressbar.isShowing()) {
			mProgressbar.cancel();
		}
		mProgressbar.setCancelable(cancelable);
		if (StringUtils.isNotBlank(message)) {
			mProgressbar.setMessage(message);
		}
		mProgressbar.show();
	}

}
