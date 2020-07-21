package com.score3s.android.asynctasks;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.score3s.android.R;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Class to provide custom progress bar throughout application.
 * 
 * @author Innoppl
 * 
 */

public class CustomProcessbar extends Dialog {

	static CustomProcessbar mCustomProcessbar;

	private CustomProcessbar mProcessbar;

	TextView mTextViewMessage;

	String mMessage;

	OnDismissListener mOnDissmissListener;

	/**
	 * Constructor with single parameter
	 * @param context
	 */
	AVLoadingIndicatorView indicator;
	private CustomProcessbar(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_processbar);

		indicator = findViewById(R.id.indicator);
		indicator.setVisibility(View.VISIBLE);

		mTextViewMessage = (TextView) findViewById(R.id.processbar_textview_message);
		this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}

	/**
	 * Constructor with two parameter
	 * @param context
	 * @param instance
	 */
	public CustomProcessbar(Context context, Boolean instance) {
		super(context);
		mProcessbar = new CustomProcessbar(context);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		if (mOnDissmissListener != null) {
			mOnDissmissListener.onDismiss(this);
		}
	}

	/**
	 * Method to show Process bar without text
	 * @param context
	 * @param cancelable
	 */
	public static void showProcessBar(Context context, boolean cancelable) {
		showProcessBar(context, cancelable, null);
	}
	


	/**
	 * Method to show Process bar with text
	 * @param context
	 * @param cancelable
	 * @param message
	 */
	public static void showProcessBar(Context context, boolean cancelable, String message) {

		if (mCustomProcessbar != null && mCustomProcessbar.isShowing()) {
			mCustomProcessbar.cancel();
		}
		mCustomProcessbar = new CustomProcessbar(context);
		mCustomProcessbar.setCancelable(cancelable);
		mCustomProcessbar.setCanceledOnTouchOutside(cancelable);
		if (StringUtils.isNotBlank(message)) {
			mCustomProcessbar.setMessage(message);
		}
		
		mCustomProcessbar.show();
		
	}

	
	/**
	 * Method to call show Process bar with callback listner
	 * @param context
	 * @param listener
	 */
	public static void showProcessBar(Context context, OnDismissListener listener) {

		if (mCustomProcessbar != null && mCustomProcessbar.isShowing()) {
			mCustomProcessbar.cancel();
		}

		if (listener == null) {
			Log.i("CustomProcessbar", "You have not set the listener for the Processbar");
		}

		mCustomProcessbar = new CustomProcessbar(context);
		mCustomProcessbar.setListener(listener);
		mCustomProcessbar.setCancelable(Boolean.TRUE);
		mCustomProcessbar.show();
	}

	/**
	 * Method to hide Process bar
	 */
	public static void hideProcessBar() {
		if (mCustomProcessbar != null && mCustomProcessbar.isShowing()) {
			mCustomProcessbar.dismiss();
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
	 * Method to set position to show the Process bar
	 * 
	 * @param view
	 */
	public static void showListViewBottomProcessBar(View view) {
		if (mCustomProcessbar != null) {
			mCustomProcessbar.dismiss();
		}

		view.setVisibility(View.VISIBLE);
	}

	/**
	 * Method to hide the listview bottom Process bar
	 * 
	 * @param view
	 */
	public static void hideListViewBottomProcessBar(View view) {
		if (mCustomProcessbar != null) {
			mCustomProcessbar.dismiss();
		}

		view.setVisibility(View.GONE);
	}

	/**
	 * Method to show the Process bar
	 * 
	 * @param context
	 * @param cancelable
	 * @param message
	 */
	public void showProcess(Context context, boolean cancelable, String message) {

		if (mProcessbar != null && mProcessbar.isShowing()) {
			mProcessbar.cancel();
		}
		mProcessbar.setCancelable(cancelable);
		if (StringUtils.isNotBlank(message)) {
			mProcessbar.setMessage(message);
		}
		mProcessbar.show();
	}

}
