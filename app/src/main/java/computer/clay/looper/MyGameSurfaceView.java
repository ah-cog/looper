package computer.clay.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MyGameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private MyGameThread myGameThread;

    private SurfaceHolder surfaceHolder;

    private int myCanvas_w, myCanvas_h;
    private Bitmap myCanvasBitmap = null;
    private Canvas myCanvas = null;
    private Matrix identityMatrix;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /* Touch Interaction Dynamics for Clay */

    private final int MAXIMUM_TOUCH_COUNT = 5;

    private boolean hasTouches = false; // i.e., a touch is detected
    private int touchCount = 0; // i.e., the number of touch points detected

    private float[] xTouch = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouch = new float[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouch = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchingAction = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_COUNT];

    private float[] xTouchPrevious = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouchPrevious = new float[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchPrevious = new boolean[MAXIMUM_TOUCH_COUNT];

    // Point where the touch started.
    private float[] xTouchStart = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouchStart = new float[MAXIMUM_TOUCH_COUNT];

    // Point where the touch ended.
    private float[] xTouchStop = new float[MAXIMUM_TOUCH_COUNT];
    private float[] yTouchStop = new float[MAXIMUM_TOUCH_COUNT];

    /**
     * "Reciprocating" is responding to an action performed by a person with their body.
     */
    public void reciprocateTouchInteraction () {

        if (hasTouches) {

            if (touchCount == 1) {

            } else if (touchCount == 2) {

            }

        }

    }

    private Substrate substrate = new Substrate ();
    private Perspective perspective = new Perspective (substrate);

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
        myCanvasBitmap = Bitmap.createBitmap(myCanvas_w, myCanvas_h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas();
        myCanvas.setBitmap (myCanvasBitmap);

        // TODO: Move setPosition to a better location!
        perspective.setPosition(myCanvas.getWidth() / 2, myCanvas.getHeight() / 2);

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
        myGameThread.setRunning(false);

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

        // Move the perspective
        myCanvas.save ();
        myCanvas.translate (perspective.getPosition ().x, perspective.getPosition ().y);
        myCanvas.scale (perspective.getScaleFactor (), perspective.getScaleFactor ());

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
        float xOrigin = 0;
        float yOrigin = 0;

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
            float loopLeft   = xOrigin +      loop.getPosition().x - loop.getRadius ();
            float loopTop    = yOrigin + -1 * loop.getPosition().y - loop.getRadius ();
            float loopRight  = xOrigin +      loop.getPosition().x + loop.getRadius ();
            float loopBottom = yOrigin + -1 * loop.getPosition().y + loop.getRadius ();
            myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, -90 + loop.getStartAngle(), loop.getAngleSpan(), false, paint);

            // Draw arrowhead on loop
            myCanvas.save();
//            myCanvas.translate(perspective.getPosition().x, perspective.getPosition().y);
            myCanvas.rotate (-1 * (360 - (loop.getStartAngle() + loop.getAngleSpan())));
            myCanvas.translate(0, -1 * loop.getRadius());

            // Set the arrowhead's style
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw the arrowhead
            myCanvas.drawLine(-20, -20, 0, 0, paint);
            myCanvas.drawLine(-20, 20, 0, 0, paint);

            myCanvas.restore();

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
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw behavior node
            myCanvas.drawCircle(action.getPosition().x, action.getPosition().y, action.getRadius(), paint);

            Loop nearestLoop = this.substrate.getLoops().get(0);
            Point nearestPoint = action.getNearestPoint(nearestLoop);

            myCanvas.drawLine (action.getPosition().x, action.getPosition().y, nearestPoint.x, nearestPoint.y, paint);
        }

        // Paint the bitmap to the "primary" canvas
        canvas.drawBitmap (myCanvasBitmap, identityMatrix, null);

        // Restore the perspective translation.
        myCanvas.restore ();

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
                if (canvas != null) {
                    onDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost (canvas);
            }
        }
    }

    // State of the person fingers/hands.
    Action touchedAction = null;

    boolean touchingAction = false; // True if touching _any_ action.
    ArrayList<Action> touchedActions = new ArrayList<Action>(); // List of the actions that are currently being touched.

    boolean movingCanvas = false; // True if not touching an action, but dragging (not just touching) the canvas.
    double maxDragDistance = 0;

    // TODO: Attach this onTouchEvent handler to the "canvas" that captures continuous motion.
    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
        int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId (pointerIndex);
        int touchAction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointCnt = motionEvent.getPointerCount ();

        if (pointCnt <= MAXIMUM_TOUCH_COUNT) {
            if (pointerIndex <= MAXIMUM_TOUCH_COUNT - 1) {

                // Get the raw touch information.
                for (int i = 0; i < pointCnt; i++) {
                    int id = motionEvent.getPointerId (i);
                    xTouchPrevious[id] = xTouch[id];
                    yTouchPrevious[id] = yTouch[id];
                    isTouchPrevious[id] = isTouch[id];

                    xTouch[id] = (motionEvent.getX (i) - perspective.getPosition ().x) / perspective.getScaleFactor (); // HACK: TODO: Get x position directly!
                    yTouch[id] = (motionEvent.getY (i) - perspective.getPosition ().y) / perspective.getScaleFactor (); // HACK: TODO: Get y position directly!
                }

                // Check if touching _any_ actions. If so, keep the canvas locked, and find the action that's being touched.
                for (Action action : this.substrate.getActions()) {
                    double distanceToTouch = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                    Log.v(null, "distanceToTouch = " + distanceToTouch);
                    if (distanceToTouch < action.getRadius() + 20) {
                        touchedActions.add (action);
                        touchingAction = true;
                    }
                }

//                // Set the canvas as moved
//                if (touchingAction == false) {
//
//                    // Calculate the drag distance
//                    double dragDistanceSquare = Math.pow(xTouch[pointerId] - xTouchStart[pointerId], 2) + Math.pow(yTouch[pointerId] - yTouchStart[pointerId], 2);
//                    dragDistance = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);
//
////                    if (this.substrate.getActions().size() > 0) { // Enable moving only if at least one action exists!
//                    if (dragDistance > 10) {
//                        // TODO: Get distance between down and current touch point. Set movingCanvas to true if the drag distance is greater than a specified threshold.
//                        movingCanvas = true;
//                    }
//                }

//                Log.v(null, "touchingAction = " + touchingAction);
//                Log.v(null, "movingCanvas = " + movingCanvas);
//                Log.v(null, "dragDistance = " + dragDistance);

                // Update the touch interaction state with the most recent raw touch information.
                if (touchAction == MotionEvent.ACTION_DOWN) {
                    isTouch[pointerId] = true;

                    xTouchStart[pointerId] = xTouch[pointerId];
                    yTouchStart[pointerId] = yTouch[pointerId];

                    isTouchingAction[pointerId] = false;

                    isDragging[pointerId] = false;
                    dragDistance[pointerId] = 0;

                    // Check if touching an action and set isTouchingAction accordingly.
                    for (Action action : this.substrate.getActions()) {
                        double distanceToAction = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
                        if (distanceToAction < action.getRadius ()) {
                            isTouchingAction[pointerId] = true;
                            break;
                        }
                    }

//                        // Get the touched actions and update their position
//                        if (touchingAction) {
//                            for (Action action : touchedActions) {
//                                action.setPosition((int) xTouch[pointerId],(int)  yTouch[pointerId]);
//                            }
//                        } else {
//                            // Move the canvas if this is a drag event!
//                            if (movingCanvas) {
//                                perspective.moveBy ((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
//                            }
//                        }
                } else if (touchAction == MotionEvent.ACTION_POINTER_DOWN) {
                    isTouch[pointerId] = true;

                } else if (touchAction == MotionEvent.ACTION_MOVE) {
                    isTouch[pointerId] = true;

                    // Calculate the drag distance
                    double dragDistanceSquare = Math.pow(xTouch[pointerId] - xTouchStart[pointerId], 2) + Math.pow(yTouch[pointerId] - yTouchStart[pointerId], 2);
                    dragDistance[pointerId] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);

                    if (dragDistance[pointerId] > 15) {
                        // TODO: Get distance between down and current touch point. Set movingCanvas to true if the drag distance is greater than a specified threshold.
                        isDragging[pointerId] = true;

                        // TODO: Move this into a separate processTouchInteraction() function, and in this event listener, only update the touch interaction state.
                        if (isTouchingAction[pointerId] == false) {
                            movingCanvas = true;
                        }
                    }

                    // TODO: Classify touch interaction behavior.

                    /*
                    // Search through all fingers and see if any fingers are dragging, and if so, set movingCanvas to true.
                    for (int i = 0; i < isDragging.length; i++) {
                        if (isDragging[i] == true) {
                            movingCanvas = true;
                        }
                    }
                    */

                    // Move the canvas if this is a drag event!
                    if (movingCanvas) {
                        perspective.moveBy ((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
                    } else if (isTouchingAction[pointerId]) {
                        for (Action action : touchedActions) {
                            action.setPosition((int) xTouch[pointerId],(int)  yTouch[pointerId]);
                        }
                    }

                    // Get the touched actions and update their position
//                    if (isDragging[pointerId]) {
//                        for (Action action : touchedActions) {
//                            action.setPosition((int) xTouch[pointerId],(int)  yTouch[pointerId]);
//                        }
//                    } else {
//                        // Move the canvas if this is a drag event!
//                        if (movingCanvas) {
//                            perspective.moveBy ((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
//                        }
//                    }

                } else if (touchAction == MotionEvent.ACTION_UP) {
                    isTouch[pointerId] = false;
                    isTouchPrevious[pointerId] = false;

                    xTouchStop[pointerId] = xTouch[pointerId];
                    yTouchStop[pointerId] = yTouch[pointerId];

//                    if (dragDistance > 15) {
//                        // TODO: Get distance between down and current touch point. Set movingCanvas to true if the drag distance is greater than a specified threshold.
//                        movingCanvas = true;
//                    }

                    // Move the canvas if this is a drag event!
                    if (movingCanvas) {
                        perspective.moveBy ((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
                    } else {
                        // Create a new action
                        this.substrate.addAction(new Action(this.substrate, (int) xTouch[pointerId], (int) yTouch[pointerId]));
                    }

//                    // Get the touched actions and update their position
//                    if (touchingAction) {
//                        for (Action action : touchedActions) {
//                            action.setPosition((int) xTouch[pointerId],(int)  yTouch[pointerId]);
//                        }
//                    } else {
//                        // Move the canvas if this is a drag event!
//                        if (movingCanvas) {
//                            perspective.moveBy ((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
//                        } else {
//                            // Create a new action
//                            this.substrate.addAction(new Action(this.substrate, (int) xTouch[pointerId], (int) yTouch[pointerId]));
//                        }
//                    }

                    /* Reset touch state for the finger. */

                    isTouchingAction[pointerId] = false;

                    isDragging[pointerId] = false;
                    dragDistance[pointerId] = 0;

                    // TODO: In processTouchInteractions, compute movingCanvas and if it is true, move the perspective.
                    movingCanvas = false;
//                    // Calculate the drag distance
//                    double dragDistanceSquare = Math.pow(xTouch[pointerId] - xTouchStart[pointerId], 2) + Math.pow(yTouch[pointerId] - yTouchStart[pointerId], 2);
//                    dragDistance[pointerId] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);
//
//                    if (dragDistance[pointerId] > 15) {
//                        // TODO: Get distance between down and current touch point. Set movingCanvas to true if the drag distance is greater than a specified threshold.
//                        isDragging[pointerId] = true;
//
//                        // TODO: Move this into a separate processTouchInteraction() function, and in this event listener, only update the touch interaction state.
//                        if (isTouchingAction[pointerId] == false) {
//                            movingCanvas = true;
//                        }
//                    }



//                    if (isDragging[pointerId]) {
//                        // Calculate the drag distance
//                        double dragDistanceSquare = Math.pow(xTouch[pointerId]  - xTouchStart[pointerId], 2) + Math.pow(yTouch[pointerId] - yTouchStart[pointerId], 2);
//                        dragDistance[pointerId] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);
//
//                        if (movingCanvas) {
//                            movingCanvas = false;
//                        }
//                    }

                } else if (touchAction == MotionEvent.ACTION_POINTER_UP) {
                    isTouch[pointerId] = false;
                    isTouchPrevious[pointerId] = false;

                } else if (touchAction == MotionEvent.ACTION_CANCEL) {
                    isTouch[pointerId] = false;
                    isTouchPrevious[pointerId] = false;


                } else {
                    isTouch[pointerId] = false;
                    isTouchPrevious[pointerId] = false;
                }
            }
        }

        return true;
    }

}