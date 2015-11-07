package com.example.janicduplessis.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //same as set-up android:screenOrientation="portrait" in <activity>, AndroidManifest.xml
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*
        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.test, getTheme())).getBitmap();
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 400, 250, false);
        ImageParser parser = new ImageParser();
        List<ColorConfig> colorConfigs = new ArrayList<>();
        colorConfigs.add(new ColorConfig(ColorType.BLUE, Color.parseColor("#0600ff"), 30, 0.5f, 0.5f));
        colorConfigs.add(new ColorConfig(ColorType.RED, Color.parseColor("#ff0000"), 30, 0.5f, 0.5f));
        colorConfigs.add(new ColorConfig(ColorType.WHITE, Color.parseColor("#ffffff"), 180, 0.2f, 0.2f));
        parser.setColorConfigs(colorConfigs);
        ImageParserResult res = parser.parseBitmap(scaled);
        String out = "";
        out += res.success ? "found " : "not found ";
        if (res.success) {
            out += res.values[Constants.ZONE_MEDIUM_TOP_LEFT] + " ";
            out += res.values[Constants.ZONE_SMALL_TOP_LEFT_1] + " ";
            out += res.values[Constants.ZONE_SMALL_TOP_LEFT_2] + " ";
            out += res.values[Constants.ZONE_BOTTOM_LEFT] + " ";
            out += res.values[Constants.ZONE_RIGHT] + " ";
        }
        Toast.makeText(getApplicationContext(), out, Toast.LENGTH_LONG).show();
*/

        setContentView(R.layout.activity_main);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction().replace(R.id.container, CameraFragment.newInstance()).commit();
        }

    }




}
