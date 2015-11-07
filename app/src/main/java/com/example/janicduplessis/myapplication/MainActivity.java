package com.example.janicduplessis.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.app.Activity.*;

import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {


    CameraManager manager = null;
    Context _context = null;
    private boolean _permissionGranted = false;
    private CameraView _cameraView = null;
    private Size _previewSize = null;
    private CameraDevice _camera = null;
    private DrawView _drawView = null;


    private final static String TAG = "SimpleCamera";
    private TextureView mTextureView = null;
    private TextureView.SurfaceTextureListener mSurfaceTextureListner = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            //Log.i(TAG, "onSurfaceTextureUpdated()");

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureSizeChanged()");
            configureTransform(width, height);
        }



        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureDestroyed()");
            return false;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureAvailable()");

            initializeCamera();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //same as set-up android:screenOrientation="portrait" in <activity>, AndroidManifest.xml
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mTextureView = (TextureView) findViewById(R.id.textureView1);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListner);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        _cameraView.closeCamera();
        _cameraView.stopBackgroundThread();

    }




    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if (mTextureView.isAvailable()) {
            initializeCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListner);
        }
    }




    private void initializeCamera()
    {
        _context = this.getBaseContext();
        manager = (CameraManager) _context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdArray = manager.getCameraIdList();
            //find the rear camera
            for (String id : cameraIdArray) {
                CameraCharacteristics specs = manager.getCameraCharacteristics(id);

                StreamConfigurationMap map = specs.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                _previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

                int Orientation = specs.get(CameraCharacteristics.LENS_FACING);
                if (Orientation == CameraCharacteristics.LENS_FACING_BACK) {
                    try {
                        ContextCompat compact = new ContextCompat();
                        if (compact.checkSelfPermission(_context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_FOR_CAMERA);
                        }else{
                            _permissionGranted = true;
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    if(_permissionGranted) {
                        manager.openCamera(id, CameraOpenCallBack, null);
                    }
                    break;
                }
            }
        } catch (CameraAccessException e) {
            //on a pas de camera sur le téléphone? what year is this? insert jumangi meme here
            e.printStackTrace();
        }
    }


    //code taken at : https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/com/example/android/camera2basic/Camera2BasicFragment.java
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = this;
        if (null == mTextureView || null == _previewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, _previewSize.getHeight(), _previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / _previewSize.getHeight(),
                    (float) viewWidth / _previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private final CameraDevice.StateCallback CameraOpenCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.i(TAG, "onOpened");

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                Log.e(TAG, "texture is null");
                return;
            }

            texture.setDefaultBufferSize(_previewSize.getWidth(), _previewSize.getHeight());
            Surface surface = new Surface(texture);

            _cameraView = new CameraView(_context, camera, surface, _previewSize.getWidth(), _previewSize.getHeight());
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_FOR_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _permissionGranted = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    _permissionGranted = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


}
