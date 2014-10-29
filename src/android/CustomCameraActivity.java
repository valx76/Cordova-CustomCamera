package com.valx76.cordova;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class CustomCameraActivity extends Activity {

	private Camera camera;
	private RelativeLayout layout;
	private FrameLayout layoutCamera;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(layoutParams);
		
		layoutCamera = new FrameLayout(this);
		FrameLayout.LayoutParams layoutCameraParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutCamera.setLayoutParams(layoutCameraParams);
		layout.addView(layoutCamera);
		
		Button btn = new Button(getApplicationContext());
		RelativeLayout.LayoutParams btnLayoutParams = new RelativeLayout.LayoutParams(0, 0);
		btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btnLayoutParams.bottomMargin = 50;
		btnLayoutParams.width = btnLayoutParams.WRAP_CONTENT;
		btnLayoutParams.height = btnLayoutParams.WRAP_CONTENT;
		btn.setLayoutParams(btnLayoutParams);
		btn.setText("Take picture");
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().putExtra(CustomCamera.ERROR_MESSAGE, "Method not implemented");
				setResult(CustomCamera.RESULT_ERROR, intent);
				finish();
			}
		});
		layout.addView(btn);
		
		setContentView(layout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			camera = Camera.open();
			
			Camera.Parameters cameraSettings = camera.getParameters();
			cameraSettings.setJpegQuality(100);
			cameraSettings.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			
			camera.setParameters(cameraSettings);
			
			layoutCamera.removeAllViews();
			layoutCamera.addView(new CustomCameraPreview(this, camera));
		} catch (Exception e) {
			Intent intent = new Intent().putExtra(CustomCamera.ERROR_MESSAGE, "Error in onResume");
			setResult(CustomCamera.RESULT_ERROR, intent);
			finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}
}