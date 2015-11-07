package com.example.janicduplessis.myapplication;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.os.HandlerThread;
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
public class CameraView{

    private SurfaceHolder _holder;
    private CameraDevice _camera;
    private List<Surface> _surfaces = new ArrayList<Surface>();
    private CameraCaptureSession _currentSession = null;
    private final static String TAG = "SimpleCamera";

    public CameraView(Context context, CameraDevice camera, Surface surface){
        _surfaces.add(surface);
        _camera = camera;
    }



    public void StopLivePreview(){
        try {
            if(_currentSession != null) {
                _currentSession.stopRepeating();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            // IllegalArgumentException	if the templateType is not supported by this device.
            // CameraAccessException	if the camera device is no longer connected or has encountered a fatal error
            // IllegalStateException	if the camera device has been closed
        }
    }



    private CameraCaptureSession.StateCallback CameraCaptureSessionCallBack = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onConfigured");
            _currentSession = session;
            CaptureRequest.Builder builder = null;
            try {
                builder = _currentSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                builder.addTarget(_surfaces.get(0));
                CaptureRequest request = builder.build();

                HandlerThread backgroundThread = new HandlerThread("CameraPreview");
                backgroundThread.start();
                Handler backgroundHandler = new Handler(backgroundThread.getLooper());

                _currentSession.setRepeatingRequest(request, CameraCaptureCallback, backgroundHandler);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.e(TAG, "CameraCaptureSession Configure failed");
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