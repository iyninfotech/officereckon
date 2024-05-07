package com.infozealrecon.android.asynctasks;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {


    private static Toast toast;

    public static void showSuccessToast(Activity context, String message) {

        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showErrorToast(Activity context, String message) {

        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

	
	
	/*
	public static void showSuccessToast(Activity context, String message) {
		if (StringUtils.isNotBlank(message)) {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) context.findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.ic_tick_green);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(message);

			Toast toast = new Toast(context);

			toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 60);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	}

	public static void showErrorToast(Activity context, String message) {
		if (StringUtils.isNotBlank(message)) {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) context.findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.ic_cross_red);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(message);

			Toast toast = new Toast(context);

			toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 60);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	}
*/
}
