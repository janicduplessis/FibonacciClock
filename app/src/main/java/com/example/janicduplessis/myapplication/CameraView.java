package com.example.janicduplessis.myapplication;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francis on 2015-11-06.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder _holder;
    private CameraDevice _camera;
    private List<Surface> _surfaces = new ArrayList<Surface>();
    private CameraCaptureSession _currentSession = null;

    public CameraView(Context context, CameraDevice camera){
        super(context);

        _camera = camera;
        //mCamera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            _holder = surfaceHolder;
            Surface s = surfaceHolder.getSurface();
            _surfaces.add(s);

            _camera.createCaptureSession(_surfaces, CameraCaptureSessionCallBack, null);

        } catch (CameraAccessException e) {
            // IllegalArgumentException	if the templateType is not supported by this device.
            // CameraAccessException	if the camera device is no longer connected or has encountered a fatal error
            // IllegalStateException	if the camera device has been closed
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if(_holder.getSurface() == null)//check if the surface is ready to receive camera data
            return;
        //first stop the preview
        StopLivePreview();

        //now, recreate the camera preview
        CaptureRequest request = CreateRequest();
        if(request != null){
            SetRequest(request);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        StopLivePreview();
       // mCamera.release();
    }

    private CaptureRequest CreateRequest(){
        try {
            CaptureRequest.Builder builder = _currentSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(_surfaces.get(0));
            CaptureRequest request = builder.build();
            return request;
        } catch (CameraAccessException e) {
            // IllegalArgumentException	if the templateType is not supported by this device.
            // CameraAccessException	if the camera device is no longer connected or has encountered a fatal error
            // IllegalStateException	if the camera device has been closed
            e.printStackTrace();
            return null;
        }
    }

    private boolean SetRequest (CaptureRequest request){
        try {
            _currentSession.setRepeatingRequest(request, CameraCaptureCallback, null);
            return true;
        } catch (CameraAccessException e) {
            // IllegalArgumentException	if the templateType is not supported by this device.
            // CameraAccessException	if the camera device is no longer connected or has encountered a fatal error
            // IllegalStateException	if the camera device has been closed
            e.printStackTrace();
            return false;
        }
    }

    private void StopLivePreview(){
        try {
            _currentSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            // IllegalArgumentException	if the templateType is not supported by this device.
            // CameraAccessException	if the camera device is no longer connected or has encountered a fatal error
            // IllegalStateException	if the camera device has been closed
        }
    }

    private final CameraCaptureSession.StateCallback CameraCaptureSessionCallBack = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            //the camera is ready to capture !!
            _currentSession = session;
            CaptureRequest request = CreateRequest();

            if(request != null) {
                SetRequest(request);
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            //something went wrong ...
        }
    };

    private final CameraCaptureSession.CaptureCallback CameraCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

        }

    };

}