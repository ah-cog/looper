package computer.clay.looper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class AppActivity extends Activity {

    AppSurfaceView mySurfaceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        Log.v("Clay", "AppActivity.onCreate");

        // Set fullscreen
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        getWindow().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set orientation
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView (R.layout.activity_main);
        mySurfaceView = (AppSurfaceView) findViewById (R.id.myview1);
    }

    @Override
    protected void onResume () {
        super.onResume ();
        mySurfaceView.AppSurfaceView_OnResume();
    }

    @Override
    protected void onPause () {
        super.onPause ();
        mySurfaceView.AppSurfaceView_OnPause();
    }

}