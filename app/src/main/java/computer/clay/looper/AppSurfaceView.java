package computer.clay.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AppSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private AppThread appThread;

    private SurfaceHolder surfaceHolder;

    private int myCanvasWidth, myCanvasHeight;
    private Bitmap myCanvasBitmap = null;
    private Canvas myCanvas = null;
    private Matrix identityMatrix;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

//    /**
//     * "Reciprocating" is responding to an action performed by a gesture with their body.
//     */
//    public void reciprocateTouchInteraction () {
//
//        if (hasTouches) {
//
//            if (touchCount == 1) {
//
//            } else if (touchCount == 2) {
//
//            }
//
//        }
//
//    }

    private Substrate substrate = new Substrate ();
    private Perspective perspective = new Perspective (this.substrate);
    private Gesture gesture = new Gesture (this.substrate, this.perspective);

    public AppSurfaceView(Context context) {
        super (context);

        Log.v("Clay", "AppSurfaceView");

//        this.substrate = new Substrate ();
//        this.perspective = new Perspective (this.substrate);

//        this.gesture = new Gesture (this.substrate, this.perspective);
    }

    public AppSurfaceView(Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public AppSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {

        myCanvasWidth = getWidth();
        myCanvasHeight = getHeight();
        myCanvasBitmap = Bitmap.createBitmap(myCanvasWidth, myCanvasHeight, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas();
        myCanvas.setBitmap (myCanvasBitmap);

        // TODO: Move setPosition to a better location!
        perspective.setPosition (myCanvas.getWidth() / 2, myCanvas.getHeight() / 2);

        identityMatrix = new Matrix();
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void AppSurfaceView_OnResume() {

        surfaceHolder = getHolder ();
        getHolder ().addCallback (this);

        // Create and start background Thread
        appThread = new AppThread(this);
        appThread.setRunning (true);
        appThread.start();

    }

    public void AppSurfaceView_OnPause() {

        // Kill the background Thread
        boolean retry = true;
        appThread.setRunning (false);

        while (retry) {
            try {
                appThread.join ();
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

        // Define base coordinate system
        float xOrigin = 0;
        float yOrigin = 0;

        // Create the default, empty loop if none exist.
        if (this.substrate.loops.size () == 0) {

            // Create a loop on the substrate if one doesn't exist.
            Loop defaultLoop = new Loop (this.substrate);
            this.substrate.addLoop(defaultLoop);
        }

        // Draw the loops
        for (Loop loop : this.substrate.getLoops()) {

            myCanvas.save ();

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
            myCanvas.rotate(-1 * (360 - (loop.getStartAngle() + loop.getAngleSpan())));
            myCanvas.translate(0, -1 * loop.getRadius());

            // Set the arrowhead's style
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw the arrowhead
            myCanvas.drawLine(-20, -20, 0, 0, paint);
            myCanvas.drawLine(-20, 20, 0, 0, paint);

            myCanvas.restore();

            // Draw the sections of the loop

            myCanvas.save ();

            if (this.perspective.loopCutPoint != null && this.perspective.loopCutSpanPoint != null) {

                // Draw the line indicating the start of the cut.

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.RED);

                myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, this.perspective.loopCutPoint.x, this.perspective.loopCutPoint.y, paint);

                // Draw the line indicating the end of the cut.

                if (this.perspective.loopCutSpan < 0) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.GREEN);
                }

                double cutStopAngle = loop.getStartAngle() + this.perspective.loopCutStartAngle + this.perspective.loopCutSpan;
                Point cutStopPoint = loop.getPoint(cutStopAngle);
                myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, cutStopPoint.x, cutStopPoint.y, paint);
                // myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, this.loopCutSpanPoint.x, this.loopCutSpanPoint.y, paint);
            }

            myCanvas.restore ();

            // TODO: Draw the activities for each loop

            myCanvas.restore ();
        }

        // Draw activities that represent Clay's current behavior.
        // TODO: Draw the activities here that are NOT on a loop. Those are drawn above.
        for (Action action : this.substrate.getActivities()) {

            // Set style
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw behavior node
            myCanvas.drawCircle(action.getPosition().x, action.getPosition().y, action.getRadius(), paint);

            /* Draw snapping path to nearest loop. */

            // Search for the nearest loop and snap to that one (ongoing).
            Loop nearestLoop = null;
            double nearestLoopDistance = Double.MAX_VALUE;
            for (Loop loop : this.substrate.getLoops()) {
                if (action.getDistanceToLoop(loop) < nearestLoopDistance) {
                    nearestLoop = loop;
                }
            }

            // Draw snapping path to nearest loop
            if (action.getDistanceToLoop (nearestLoop) < 250) {
                Point nearestPoint = action.getNearestPoint (nearestLoop);
                myCanvas.drawLine(action.getPosition().x, action.getPosition().y, nearestPoint.x, nearestPoint.y, paint);
            }
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
        // The function run in background thread, not UI thread.

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

    // State of the gesture fingers/hands.
//    Action touchedAction = null;
//
//    boolean touchingAction = false; // True if touching _any_ action.
//    ArrayList<Action> touchedActivities = new ArrayList<Action> (); // List of the activities that are currently being touched.
//
//    boolean isPerformingPerspectiveGesture = false;
//    boolean isMovingPerspective = false; // True if not touching an action, but dragging (not just touching) the canvas.
//    double maxDragDistance = 0;
//    boolean isCreatingLoopPerspective = false;

    // TODO: Attach this onTouchEvent handler to the "canvas" that captures continuous motion.
    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
        int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId (pointerIndex);
        int touchAction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointCount = motionEvent.getPointerCount ();

        int MAXIMUM_TOUCH_COUNT = 5;

        double[] xTouches = new double[MAXIMUM_TOUCH_COUNT];
        double[] yTouches = new double[MAXIMUM_TOUCH_COUNT];

        if (pointCount <= MAXIMUM_TOUCH_COUNT) {
            if (pointerIndex <= MAXIMUM_TOUCH_COUNT - 1) {

                // Update touch action state with the raw touch information provided by the operating platform (i.e., Android).
                for (int i = 0; i < pointCount; i++) {
                    int id = motionEvent.getPointerId (i);
//                    xTouchPrevious[id] = xTouch[id];
//                    yTouchPrevious[id] = yTouch[id];
//                    isTouchPrevious[id] = isTouch[id];
//
//                    xTouch[id] = (motionEvent.getX (i) - perspective.getPosition ().x) / perspective.getScaleFactor (); // HACK: TODO: Get x position directly!
//                    yTouch[id] = (motionEvent.getY (i) - perspective.getPosition ().y) / perspective.getScaleFactor (); // HACK: TODO: Get y position directly!

                    xTouches[id] = (motionEvent.getX (i) - perspective.getPosition ().x) / perspective.getScaleFactor (); // HACK: TODO: Get x position directly!
                    yTouches[id] = (motionEvent.getY (i) - perspective.getPosition ().y) / perspective.getScaleFactor (); // HACK: TODO: Get y position directly!

//                    gesture.touch (id, xTouch, yTouch);
                }

//                // Check if touching _any_ activities (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
//                for (Action action : this.substrate.getActivities ()) {
//                    double distanceToTouch = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                    if (distanceToTouch < action.getRadius () + 20) {
//                        touchedActivities.add (action);
//                        touchingAction = true;  // TODO: Set state of finger
////                        action.state = Action.State.MOVING; // Set state of touched action
//                    }
//                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchAction == MotionEvent.ACTION_DOWN) {

                    gesture.touch (pointerId, xTouches[pointerId], yTouches[pointerId]);

//                    // Update touch action state.
//                    isTouch[pointerId] = true;
//
//                    xTouchStart[pointerId] = xTouch[pointerId];
//                    yTouchStart[pointerId] = yTouch[pointerId];

                    gesture.classify (pointerId);

//                    isTouchingAction[pointerId] = false;
//
//                    isDragging[pointerId] = false;
//                    dragDistance[pointerId] = 0;
//
//                    // Check if touching _any_ activities (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
//                    for (Action dot : this.substrate.getActivities()) {
//                        double distanceToTouch = dot.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                        if (distanceToTouch < dot.getRadius () + 20) {
//
////                            isTouchingAction[pointerId] = true; // TODO: Set state of finger
//
//                            if (!this.touchedActivities.contains(dot)) {
//                                touchedActivities.add (dot);
//                            }
//
//                            touchingAction = true;  // TODO: Set state of finger
////                        dot.state = Action.State.MOVING; // Set state of touched dot
//                        }
//                    }
//
//                    // Check if touching an action and set isTouchingAction accordingly.
//                    for (Action dot : this.substrate.getActivities()) {
//                        double distanceToAction = dot.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                        if (distanceToAction < dot.getRadius ()) {
//                            isTouchingAction[pointerId] = true; // TODO: Set state of finger
////                            dot.state = Action.State.MOVING; // Set state of touched dot
//                            break;
//                        }
//                    }
//
//                    // Check if touching in a loop.
//                    for (Loop loop: this.substrate.getLoops()) {
//                        double distanceToTouch = loop.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                        Log.v ("Clay", "distanceToTouch = " + distanceToTouch);
//                        if (distanceToTouch < 0.50 * loop.getRadius ()) {
//
//                            Log.v ("Clay", "starting loop gesture");
//                            isPerformingLoopGesture = true;
//                            selectedLoop = loop;
//
////                            isTouchingAction[pointerId] = true; // TODO: Set state of finger
//
////                            if (!this.touchedLoops.contains(loop)) {
////                                touchedLoops.add(loop);
////                            }
//
//                            touchingAction = true;  // TODO: Set state of finger
////                        action.state = Action.State.MOVING; // Set state of touched action
//                        }
//                    }

                } else if (touchAction == MotionEvent.ACTION_POINTER_DOWN) {

//                    isTouch[pointerId] = true;

                } else if (touchAction == MotionEvent.ACTION_MOVE) {

                    gesture.touch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    gesture.classify (pointerId);

                    // Update touch action state.
//                    isTouch[pointerId] = true;

//                    // Calculate the drag distance
//                    double dragDistanceSquare = Math.pow(xTouch[pointerId] - xTouchStart[pointerId], 2) + Math.pow(yTouch[pointerId] - yTouchStart[pointerId], 2);
//                    dragDistance[pointerId] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);

//                    // Check if a drag is occurring (defined by continuously touching the screen while deviating from the initail point of touch by more than 15 pixels)
//                    if (dragDistance[pointerId] > 15) {
//                        // TODO: Get distance between down and current touch point. Set isMovingPerspective to true if the drag distance is greater than a specified threshold.
//                        isDragging[pointerId] = true;
//
//                        // TODO: Move this into a separate processTouchInteraction() function, and in this event listener, only update the touch interaction state.
//
//                        // Check if a loop gesture is being performed.
//                        if (isPerformingLoopGesture) {
//
//                            // Get the distance from the center of the loop "selected" with the gesture.
//                            previousDistanceToSelectedLoopCenter = selectedLoop.getDistance ((int) xTouchPrevious[pointerId], (int) yTouchPrevious[pointerId]);
//                            distanceToSelectedLoopCenter = selectedLoop.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//
////                            Log.v ("Clay", "distanceToLoopCenter = " + distanceToLoopCenter);
//                            if (previousDistanceToSelectedLoopCenter < selectedLoop.getRadius() && distanceToSelectedLoopCenter > selectedLoop.getRadius ()) {
//                                Log.v("Clay", "Cut the loop.");
//
//                                isCreatingLoopPerspective = true;
//
//                                // TODO: Get the angle and (x,y) coordinate at which the loop was crossed (exited).
//                                if (loopCutPoint == null) {
//                                    loopCutPoint = new Point ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                                    loopCutStartAngle = (int) selectedLoop.getAngle((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                                    Log.v ("Clay", "loopCutStartAngle = " + loopCutStartAngle);
//                                }
//
//                                // TODO: Calculate loopCutStartAngle
//
//                            } else if (previousDistanceToSelectedLoopCenter > selectedLoop.getRadius () && distanceToSelectedLoopCenter < selectedLoop.getRadius ()) {
//                                Log.v ("Clay", "Uncut the loop.");
//
//                                // Clear the angle and (x,y) coordinate at which the loop was crossed (entered).
//                                if (loopCutPoint != null) {
//                                    loopCutPoint = null;
//                                    loopCutSpanPoint = null;
//                                    loopCutStartAngle = 0;
//                                    loopCutSpan = 0;
//                                }
//
//                            }
//
//                            // If started cutting the loop, then calculate the angle offset of the cut in degrees.
//                            if (loopCutPoint != null) {
//
//                                Point currentTouchPoint = new Point ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                                // TODO: Calculate the end angle between the three points (loop center, loopCutPoint, and the current touch point)
//                                loopCutSpan = (int) selectedLoop.getAngle(loopCutPoint, currentTouchPoint);
//                                // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
//                                Log.v ("Clay", "loopCutStartAngle = " + loopCutStartAngle);
//                                loopCutSpanPoint = selectedLoop.getPoint (loopCutSpan); // (loopCutStartAngle + loopCutSpan);
//                                Log.v ("Clay", "angle = " + loopCutSpan);
//                            }
//
//                        } else {
//
//                            // If a loop gesture is not being performed, then it must be the case that the perspective is being moved.
//                            if (isTouchingAction[pointerId] == false) {
//                                isPerformingPerspectiveGesture = true;
//                                isMovingPerspective = true;
//                            }
//                        }
//                    }

                    // TODO: Classify touch interaction behavior.

                    /*
                    // Search through all fingers and see if any fingers are dragging, and if so, set isMovingPerspective to true.
                    for (int i = 0; i < isDragging.length; i++) {
                        if (isDragging[i] == true) {
                            isMovingPerspective = true;
                        }
                    }
                    */

//                    // Move the perspective over the canvas if this is a drag event!
//                    if (isPerformingPerspectiveGesture) {
//
//                        if (isMovingPerspective) {
//                            perspective.moveBy((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
//                        }
//
//                    } else if (isPerformingLoopGesture) {
//
//                        // TODO: Start constructing a viewing angle model to use to construct a viewing angle.
//
//                        // TODO: Look for the point on the loop at which the finger crosses the line (i.e., the distance is greater than the loop's radius).
//
//                    } else if (isTouchingAction[pointerId]) {
//                        for (Action dot : touchedActivities) {
//                            dot.setPosition ((int) xTouch[pointerId],(int)  yTouch[pointerId]);
//                        }
//                    }

                } else if (touchAction == MotionEvent.ACTION_UP) {

                    gesture.untouch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    gesture.classify(pointerId);

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;
//
//                    xTouchStop[pointerId] = xTouch[pointerId];
//                    yTouchStop[pointerId] = yTouch[pointerId];

//                    // Move the canvas if this is a drag event!
//                    if (isPerformingPerspectiveGesture) {
//
//                        if (isMovingPerspective) {
//                            perspective.moveBy ((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
//                        }
//
//                    } else if (isPerformingLoopGesture) {
//
//                        if (isCreatingLoopPerspective) {
//                            // TODO: Create the loop perspective and associate with the substrate perspective.
//
//                            loopCutPoint = null;
//                            loopCutSpanPoint = null;
//
//                            loopCutStartAngle = 0;
//                            loopCutSpan = 0;
//
//                            // Update the gesture state
//                            isPerformingLoopGesture = false;
//                            selectedLoop = null;
//                        }
//
//                    } else {
//
//                        // TODO: If moving an action, upon release, call "searchForPosition()" to check the "logical state" of the action in the substrate WRT the other loops, and find it's final position and update its state (e.g., if it's near enough to snap to a loop, to be deleted, etc.).
//
//                        Log.v("Clay", "before isTouchingAction[pointerId]");
//                        if (touchedActivities.size() > 0) { // if (isTouchingAction[pointerId]) {
//                            Log.v("Clay", "touchedActivities.size() = " + touchedActivities.size());
//
//                            // Settle position of action.
//                            for (Action dot : touchedActivities) {
//                                dot.settlePosition();
//                            }
//
//                            // HACK: This hack removes _all_ touched activities when _any_ finger is lifted up.
//                            touchedActivities.clear ();
//                            // TODO: Remove specific finger from the list of fingers touching down.
//
//                            // HACK: This hack updates the touch flag that indicates if _any_ finger is touching to false.
//                            touchingAction = false;
//                            // TODO: Set state of finger
//
////                            // Update the gesture state
////                            isPerformingLoopGesture = false;
////                            selectedLoop = null;
//
//                        } else {
//
//                            // Add an action to the substrate.
//                            Action newDot = new Action(this.substrate, (int) xTouch[pointerId], (int) yTouch[pointerId]);
//                            this.substrate.addAction (newDot);
//                            newDot.settlePosition ();
//                        }
//
////                        if (isTouchingAction[pointerId]) {
////                            // Settle the action.
////                            // Check if touching an action and set isTouchingAction accordingly.
////                            for (Action action : this.substrate.getActivities()) {
////                                double distanceToAction = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
////                                if (distanceToAction < action.getRadius ()) {
//////                                    action.settlePosition ();
////                                    break;
////                                }
////                            }
////                        } else {
////                            // Create a new action
////                            this.substrate.addAction(new Action(this.substrate, (int) xTouch[pointerId], (int) yTouch[pointerId]));
////                        }
//                    }
//
//                    /* Reset touch state for the finger. */
//
//                    isTouchingAction[pointerId] = false;
//
//                    isDragging[pointerId] = false;
//                    dragDistance[pointerId] = 0;
//
//                    // TODO: In processTouchInteractions, compute isMovingPerspective and if it is true, move the perspective.
//                    isPerformingPerspectiveGesture = false;
//                    isMovingPerspective = false;
//                    isPerformingLoopGesture = false;
//                    isCreatingLoopPerspective = false;

                } else if (touchAction == MotionEvent.ACTION_POINTER_UP) {

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;

                } else if (touchAction == MotionEvent.ACTION_CANCEL) {

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;

                } else {

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;

                }
            }
        }

        return true;
    }

}