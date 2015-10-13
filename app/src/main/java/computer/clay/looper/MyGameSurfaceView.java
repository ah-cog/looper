package computer.clay.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyGameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;

    private MyGameThread myGameThread;

    private int myCanvas_w, myCanvas_h;
    private Bitmap myCanvasBitmap = null;
    private Canvas myCanvas = null;
    private Matrix identityMatrix;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // In this test, handle maximum of 2 pointer
    private final int MAXIMUM_TOUCH_COUNT = 5;

    private float[] xTouch = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouch = new float[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouch = new boolean[MAXIMUM_TOUCH_COUNT];

    private float[] x_last = new float[MAXIMUM_TOUCH_COUNT];
    private float[] y_last = new float[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouch_last = new boolean[MAXIMUM_TOUCH_COUNT];

    private float[] xTouchDown = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouchDown = new float[MAXIMUM_TOUCH_COUNT];

    private float[] xTouchUp = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouchUp = new float[MAXIMUM_TOUCH_COUNT];

    private Substrate substrate = new Substrate ();

    public MyGameSurfaceView (Context context) {
        super (context);
    }

    public MyGameSurfaceView (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public MyGameSurfaceView (Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {

        myCanvas_w = getWidth();
        myCanvas_h = getHeight();
        myCanvasBitmap = Bitmap.createBitmap (myCanvas_w, myCanvas_h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas();
        myCanvas.setBitmap (myCanvasBitmap);

        identityMatrix = new Matrix();
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void MyGameSurfaceView_OnResume () {

        surfaceHolder = getHolder ();
        getHolder ().addCallback (this);

        // Create and start background Thread
        myGameThread = new MyGameThread (this);
        myGameThread.setRunning (true);
        myGameThread.start ();

    }

    public void MyGameSurfaceView_OnPause () {

        // Kill the background Thread
        boolean retry = true;
        myGameThread.setRunning (false);

        while (retry) {
            try {
                myGameThread.join ();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
    }

    @Override
    protected void onDraw (Canvas canvas) {

        // Draw the background
        myCanvas.drawColor (Color.WHITE);

        /*
        if(isTouch[0]) {
            if(isTouch_last[0]) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                paint.setColor(Color.RED);
                myCanvas.drawLine(x_last[0], y_last[0], xTouch[0], yTouch[0], paint);
                circles.add(new Point(Math.round(xTouch[0]), Math.round(yTouch[0])));
            }
        }
        */

        // Define base coordinate system
        float xOrigin = (myCanvas.getWidth() / 2);
        float yOrigin = (myCanvas.getHeight() / 2);

//        myCanvas.save ();
//        myCanvas.translate (xOrigin, yOrigin);

        // Create the default, empty loop if none exist.
        if (this.substrate.loops.size () == 0) {

            // Create a loop on the substrate if one doesn't exist.
            Loop defaultLoop = new Loop (this.substrate);
            this.substrate.addLoop(defaultLoop);
        }

        // Draw the loops
        for (Loop loop : this.substrate.getLoops()) {

            // Set the loop's style
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw the loop
            float loopLeft = xOrigin + loop.getPosition().x - loop.getRadius ();
            float loopTop = yOrigin + -1 * loop.getPosition().y - loop.getRadius ();
            float loopRight = xOrigin + loop.getPosition().x + loop.getRadius ();
            float loopBottom = yOrigin + -1 * loop.getPosition().y + loop.getRadius ();
            myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, -90 + loop.getStartAngle (), loop.getAngleSpan (), false, paint);

            // TODO: Draw arrowhead on loop

            // TODO: Draw the actions for each loop
        }

//        myCanvas.save();
//        myCanvas.translate(50, 250);
//        myCanvas.rotate(45);
//        myCanvas.drawLine(0, 0, 50, 50, paint);
//        myCanvas.drawLine(0, 0, 50, -50, paint);
//        myCanvas.restore();

        // Draw actions that represent Clay's current behavior.
        // TODO: Draw the actions here that are NOT on a loop. Those are drawn above.
        for (Action action : this.substrate.getActions ()) {

            // Set style
            paint.setStyle (Paint.Style.STROKE);
            paint.setStrokeWidth (2);
            paint.setColor (Color.BLACK);

            // Draw behavior node
            myCanvas.drawCircle (action.getPosition().x, action.getPosition().y, action.getRadius (), paint);
        }

        // Paint the bitmap to the "primary" canvas
        canvas.drawBitmap (myCanvasBitmap, identityMatrix, null);

    }

    public void updateStates () {
        // Dummy method() to handle the States
    }

    public void updateSurfaceView () {
        //The function run in background thread, not ui thread.

        Canvas canvas = null;

        try {
            canvas = surfaceHolder.lockCanvas ();

            synchronized (surfaceHolder) {
                updateStates ();
                onDraw (canvas);
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost (canvas);
            }
        }
    }

    // TODO: Attach this onTouchEvent handler to the "canvas" that captures continuous motion.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK)
                >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int touchAction = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        int pointCnt = motionEvent.getPointerCount();

        if (pointCnt <= MAXIMUM_TOUCH_COUNT){
            if (pointerIndex <= MAXIMUM_TOUCH_COUNT - 1) {

                for (int i = 0; i < pointCnt; i++) {
                    int id = motionEvent.getPointerId(i);
                    x_last[id] = xTouch[id];
                    y_last[id] = yTouch[id];
                    isTouch_last[id] = isTouch[id];
                    xTouch[id] = motionEvent.getX(i);
                    yTouch[id] = motionEvent.getY(i);
                }

                switch (touchAction){
                    case MotionEvent.ACTION_DOWN:
                        isTouch[pointerId] = true;
                        xTouchDown[pointerId] = xTouch[pointerId];
                        yTouchDown[pointerId] = yTouch[pointerId];
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isTouch[pointerId] = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isTouch[pointerId] = true;

                        // TODO: Update the position of the latest "down" action.

                        // TODO: Update the position of the action touched (if any).
                        Action touchedAction = null;
                        for (Action action : this.substrate.getActions()) {

                            double distanceToAction = action.getDistance ((int) x_last[pointerId], (int) y_last[pointerId]);

                            if (distanceToAction < action.getRadius()) {
                                // NOTE: The action was touched.
                                touchedAction = action;
                                touchedAction.setPosition((int) x_last[pointerId],(int)  y_last[pointerId]);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;

                        xTouchUp[pointerId] = xTouch[pointerId];
                        yTouchUp[pointerId] = yTouch[pointerId];

                        double distanceSquare = Math.pow(xTouchUp[pointerId] - xTouchDown[pointerId], 2) + Math.pow(yTouchUp[pointerId] - yTouchDown[pointerId], 2);
                        double distance = (distanceSquare != 0 ? Math.sqrt(distanceSquare) : 0);

                        if (distance < 10) {
                            // A single touch occurred.
                            // Either an action has been touched or one will be created.
//                            actions.add (new Point (Math.round (xTouchUp[pointerId]), Math.round (yTouchUp[pointerId])));
//                            actions.add (new Action(this.substrate, (int) xTouchUp[pointerId], (int) yTouchUp[pointerId]));

                            // Check for the nearest touched node
                            touchedAction = null;
                            for (Action action : this.substrate.getActions()) {

                                double distanceToAction = action.getDistance ((int) xTouchUp[pointerId], (int) yTouchUp[pointerId]);

                                if (distanceToAction < action.getRadius()) {
                                    // NOTE: The action was touched.
                                    touchedAction = action;
                                    touchedAction.moveBy(50, 50);
                                }
                            }

                            if (touchedAction != null) {
                                // TODO: Move the action.
                            } else {
                                // Create a new action
                                this.substrate.addAction(new Action(this.substrate, (int) xTouchUp[pointerId], (int) yTouchUp[pointerId]));
                            }

                        } else {
                            // Drag action is occurring.
                            // Either an action or a the canvas is being moved.

                            // TODO: Check for a collision with an action.
//                            for (Action action : actions) {
//                                double distanceToActionSquare = Math.pow (xTouchDown[pointerId] - action.getPosition().x, 2) + Math.pow (yTouchDown[pointerId] - action.getPosition().y, 2);
//                                double distanceToAction = Math.sqrt(distanceToActionSquare);
//
//                                // Check if the screen was touched within the action's radius.
//                                if (distanceToAction <= actionRadius) {
//
//                                }
//                            }

                            // TODO: If an action is not being touch, then move the perspective!

//                            loopPositionX = loopPositionX + (xTouchUp[pointerId] - xTouchDown[pointerId]);
//                            loopPositionY = loopPositionY - (yTouchUp[pointerId] - yTouchDown[pointerId]);
                        }


//                        actions.add (new Point (Math.round (xTouch[pointerId]), Math.round (yTouch[pointerId])));

                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                        break;
                    default:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                }
            }
        }

        return true;
    }

}