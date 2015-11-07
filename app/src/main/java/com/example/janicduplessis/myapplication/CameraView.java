package com.example.janicduplessis.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by francis on 2015-11-06.
 */
public class CameraView{

    private SurfaceHolder _holder;
    private CameraDevice _camera;
    private List<Surface> _surfaces = new ArrayList<Surface>();
    private CameraCaptureSession _currentSession = null;
    private final static String TAG = "SimpleCamera";
    private HandlerThread _backgroundThreadCamera = null;
    private HandlerThread _backgroundThreadImageReader = null;
    private Handler _backgroundHandlerImageReader = null;
    private Handler _backgroundHandlerCamera = null;
    private ImageReader _reader = null;
    private Semaphore _cameraOpenCloseLock = new Semaphore(1);

    public CameraView(Context context, CameraDevice camera, Surface surface, int width, int height){

        //_backgroundThreadImageReader = new HandlerThread("CameraImageReader");
        //_backgroundThreadImageReader.start();
        //_backgroundHandlerImageReader = new Handler(_backgroundThreadImageReader.getLooper());


        //_reader = ImageReader.newInstance(width,height, ImageFormat.JPEG,2);
       // _reader.setOnImageAvailableListener(ImageReaderListener, _backgroundHandlerImageReader);

       // _surfaces.add(_reader.getSurface());
        _surfaces.add(surface);
        _camera = camera;


        try {
            _camera.createCaptureSession(_surfaces, CameraCaptureSessionCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }




    public HandlerThread getBackgroundThread()
    {
        return this._backgroundThreadCamera;
    }


    private CameraCaptureSession.StateCallback CameraCaptureSessionCallBack = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onConfigured");
            try {
                CaptureRequest.Builder builder = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                builder.addTarget(_surfaces.get(0));
                CaptureRequest request = builder.build();


                _backgroundThreadCamera = new HandlerThread("CameraPreview");
                _backgroundThreadCamera.start();
                _backgroundHandlerCamera = new Handler(_backgroundThreadCamera.getLooper());

                session.setRepeatingRequest(request, CameraCaptureCallback, _backgroundHandlerCamera);

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

    /*
    private CameraCaptureSession.StateCallback CameraCaptureSessionBurstCallBack = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            CaptureRequest.Builder builder = null;
            try {
                builder = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                builder.addTarget(_surfaces.get(1));
                CaptureRequest request = builder.build();

                final CameraCaptureSession ses = session;
                final CaptureRequest req = request;
                final Handler h = new Handler();
                final int delay = 1000; //milliseconds

                h.postDelayed(new Runnable() {
                    public void run() {
                        //do something
                        try {
                            ses.capture(req, CameraCaptureBurstCallBack, null);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                        h.postDelayed(this, delay);
                    }
                }, delay);


            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };
    */


    private final CameraCaptureSession.CaptureCallback CameraCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.i("fail", failure.toString());
        }
    };

   // private final CameraCaptureSession.CaptureCallback CameraCaptureBurstCallBack = new CameraCaptureSession.CaptureCallback() {
   //     @Override
   //     public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
   //         super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
   //     }
  //  };




    private ImageReader.OnImageAvailableListener ImageReaderListener = new OnImageAvailableListener(){
        @Override
        public void onImageAvailable(ImageReader reader) {

            //holy fuck this is cpu intensive !!

            //Image image = null;
            //Bitmap bitmap = null;
            //image = reader.acquireLatestImage();
            //Image.Plane[] planes = image.getPlanes();
           // Buffer buffer = planes[0].getBuffer().rewind();
            //maybe ici on pourrais redimentionner l'image, au lieux de pogner le full picture ...
           // bitmap = Bitmap.createBitmap(reader.getWidth(), reader.getHeight(), Bitmap.Config.ARGB_8888);
            //sinon on peut utiliser Bitmap.CreateScaledBitmap !
            //bitmap = Bitmap.createScaledBitmap(bitmap, DesiredWidth, DesiredHeight, false);

            //hack, pcq j'avais des expceptions comme quoi Buffer not large enough for pixels
            //http://stackoverflow.com/questions/12208619/buffer-not-large-enough-for-pixels
            //buffer = ByteBuffer.allocate(bitmap.getRowBytes() * bitmap.getHeight() * 2);
            //try {
             //   bitmap.copyPixelsFromBuffer(buffer);
            //}catch(Exception ex)
           // {
            //    ex.printStackTrace();
           // }

            //process Image, Algo de janic ... ça plante en ce moment ...
           // ImageParser parser = new ImageParser();
           // parser.parseBitmap(bitmap);

        }
    };




    public void closeCamera(){
        try {
            _cameraOpenCloseLock.acquire();
            if (null != _currentSession) {
                _currentSession.close();
                _currentSession = null;
            }
            if (null != _camera) {
                _camera.close();
                _camera = null;
            }
            if (null != _reader) {
                _reader.close();
                _reader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            _cameraOpenCloseLock.release();
        }
    }

    public void stopBackgroundThread(){
        _backgroundThreadCamera.quitSafely();
        //_backgroundThreadImageReader.quitSafely();
        try {
            _backgroundThreadCamera.join();
           // _backgroundThreadImageReader.join();
            _backgroundThreadCamera = null;
           // _backgroundThreadImageReader = null;
            //_backgroundHandlerImageReader = null;
            _backgroundHandlerCamera = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}