package com.valx76.cordova;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CustomCamera extends CordovaPlugin {

    public static final int RESULT_ERROR = 1337;
    public static final String ERROR_MESSAGE = "ERROR";

    private CallbackContext callbackContext;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (!hasCamera()) {
			callbackContext.error("No camera detected");
			return false;
		}

		this.callbackContext = callbackContext;

		if ("cam".equals(action)) {
			Context context = cordova.getActivity().getApplicationContext();
			Intent intent = new Intent(context, CustomCameraActivity.class);
			cordova.startActivityForResult(this, intent, 0);

			return true;
		}

		return false;
    }

    private boolean hasCamera() {
    	Context context = cordova.getActivity().getApplicationContext();
    	PackageManager manager = context.getPackageManager();

    	return manager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (resultCode == Activity.RESULT_OK) {
    		callbackContext.success("OK");
    	} else if (resultCode == RESULT_ERROR) {
    		String strError = intent.getExtras().getString(ERROR_MESSAGE);

    		if (strError != null) {
    			callbackContext.error(strError);
    		} else {
    			callbackContext.error("Unknown error");
    		}
    	}
    }
}
