package com.example.janicduplessis.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

public class MainActivity extends AppCompatActivity {


    CameraManager manager = null;
    Context _context = null;
    private boolean _permissionGranted = false;
    private CameraView _cameraView = null;
    private Size _previewSize = null;
    private CameraDevice _camera = null;


    private final static String TAG = "SimpleCamera";
    private TextureView mTextureView = null;
    private TextureView.SurfaceTextureListener mSurfaceTextureListner = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            //Log.i(TAG, "onSurfaceTextureUpdated()");

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                                int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureSizeChanged()");

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureDestroyed()");
            return false;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                              int height) {
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

        if (_camera != null)
        {
            if(_cameraView != null){
                _cameraView.StopLivePreview();
            }
            _camera.close();
            _camera = null;
            _cameraView = null;
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
                CameraCharacteristics specs = this.manager.getCameraCharacteristics(id);

                StreamConfigurationMap map = specs.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size _previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

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


            _cameraView = new CameraView(_context, camera, surface);
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
