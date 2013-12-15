package com.example.cameraexample;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.Surface;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera = null;

    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;

    private String TAG = "CameraExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "MainActivity onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        Log.v(TAG, "Try to open camera");

        try {
            mCamera = Camera.open();
        }
        catch (Exception e) {
            Log.e(TAG, "Could not get camera" + e.getMessage());
        }

        setContentView(R.layout.activity_main);

        mSurfaceView = (SurfaceView) findViewById(R.id.preview_view);

        if (mCamera != null && mSurfaceView != null) {

            mSurfaceHolder = mSurfaceView.getHolder();
            if (mSurfaceHolder != null) {
                mSurfaceHolder.addCallback(this);
                mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                // mSurfaceHolder.setFixedSize(320, 240);

                setCameraDisplayOrientation(0, mCamera);
            } else {
                Log.e(TAG, "Could not get surfaceholder");
            }
        } else {
            Log.v(TAG, "mCamera or mSurfaceView is null");
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "MainActivity onStop");
        Log.v(TAG, "Try to release camera");

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onStop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mSurfaceHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.v(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        mSurfaceHolder = null;
    }

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
