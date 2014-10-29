package com.valx76.cordova;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android.util.Log;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera.CameraInfo;


public class CustomCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static final int SLEEP_TIME = 50;

	private final Camera camera;
	private Face[] faces;
	private Paint paint;
	private Thread thread;
	

	public CustomCameraPreview(Context context, Camera camera) {
		super(context);
		this.camera = camera;

		// Creates a Paint (used to stylize the squares around faces)
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(3);
		paint.setAlpha(128);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);


		camera.setFaceDetectionListener(new FaceDetectionListener() {
			@Override
			public void onFaceDetection(Face[] faces, Camera camera) {
				setFaces(faces);
			}
		});


		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/* 
	Creates a thread to indirectly call the "onDraw" method.
	SurfaceView subclasses are not calling onDraw automatically.
	*/
	private void createUIRefreshThread() {
		if (thread != null) {
			thread.interrupt();
		}

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(SLEEP_TIME);
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		thread.start();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Needed to use the "onDraw" method
		setWillNotDraw(false);


		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();

			startFaceDetection();

			createUIRefreshThread();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.interrupt();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (getHolder().getSurface() == null)	return;

		try {
			camera.stopPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			camera.setPreviewDisplay(holder);
			camera.setDisplayOrientation(90);
			camera.startPreview();

			startFaceDetection();

			createUIRefreshThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Tests if the device supports face detection and starts it
	public void startFaceDetection() {
		Camera.Parameters params = camera.getParameters();
		
		// Check if the device supports face detection
		if (params.getMaxNumDetectedFaces() > 0) {
			camera.startFaceDetection();
			Toast.makeText(getContext(), "Starting face detection", Toast.LENGTH_SHORT).show();
		}
	}

	// Keeps the detected faces in an array and refresh the screen
	public void setFaces(Face[] detectedFaces) {
		faces = detectedFaces;
		invalidate();
	}

	// Draws red squares around detected faces
	@Override
	protected void onDraw(Canvas canvas) {
		// Help: http://developer.android.com/reference/android/hardware/Camera.Face.html#rect

		super.onDraw(canvas);

		if (faces != null && faces.length > 0) {
			int vWidth = getWidth();
			int vHeight = getHeight();

			Matrix matrix = new Matrix();
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(0, info);
			
			boolean mirror = (info.facing == CameraInfo.CAMERA_FACING_BACK);
			
			matrix.setScale(mirror ? -1 : 1, 1);
			matrix.postRotate(info.orientation);
			matrix.postScale(getWidth() / 2000f, getHeight() / 2000f);
			matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);

			RectF rect = new RectF();

			for (int i = 0; i < faces.length; i++) {
				rect.set(faces[i].rect);

				// Workaround (forced Portrait orientation)
				rect.left = -rect.left;
				rect.right = -rect.right;

				matrix.mapRect(rect);
				canvas.drawRect(rect, paint);
			}
		}
	}
}
