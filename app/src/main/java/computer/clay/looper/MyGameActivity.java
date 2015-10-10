package computer.clay.looper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MyGameActivity extends Activity {

    MyGameSurfaceView mySurfaceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // Set fullscreen
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        getWindow().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set orientation
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView (R.layout.activity_main);
        mySurfaceView = (MyGameSurfaceView) findViewById (R.id.myview1);
    }

    @Override
    protected void onResume () {
        // TODO Auto-generated method stub
        super.onResume ();
        mySurfaceView.MyGameSurfaceView_OnResume ();
    }

    @Override
    protected void onPause () {
        // TODO Auto-generated method stub
        super.onPause ();
        mySurfaceView.MyGameSurfaceView_OnPause ();
    }

}